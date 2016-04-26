package ru.yamoney.test.services.card_pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yamoney.test.repository.OperationRepository;
import ru.yamoney.test.repository.PaymentRepository;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireResponse;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireService;

import java.util.Date;
import java.util.List;

/**
 * Created by nizienko on 26.04.2016.
 */

@Service
@EnableScheduling
public class ParallelClearingServiceImpl implements ClearingService {
    private static final Logger LOG = LoggerFactory.getLogger(ParallelClearingServiceImpl.class);

    @Autowired
    @Qualifier("paymentRepository")
    private PaymentRepository paymentRepository;

    @Autowired
    @Qualifier("operationRepository")
    private OperationRepository<Operation> operationRepository;

    @Autowired
    @Qualifier("moscowBankAcquireService")
    private BankAcquireService bankAcquireService;

    @Scheduled(fixedDelay = 10000)
    public void clearPayments() {
        paymentRepository.fetchForClearing().parallelStream().forEach(o ->
        {
            final Payment payment = (Payment) o;
            LOG.info(String.format("Найден платеж для которого требуется провести клиринг: %s", payment));
            List<Operation> operations = operationRepository.getByPaymentId(payment.getId());
            LOG.info(String.format("В платеже %s сущесвтуют операции: %s", payment.getId(), operations));

            LOG.info("Создаем новую операцию клиринга");

            Operation clearingOperation = new Operation();
            clearingOperation.setPaymentId(payment.getId());
            clearingOperation.setOperationType(Operation.OperationType.CLEAR);
            clearingOperation.setStatus(Operation.Status.CREATED);
            clearingOperation.setCreatedDate(new Date());

            operationRepository.insert(clearingOperation);

            operations = operationRepository.getByPaymentId(payment.getId());

            Operation authorizeOperation = null;
            clearingOperation = null;
            for (Operation operation : operations) {
                if (operation.getOperationType() == Operation.OperationType.CLEAR) {
                    clearingOperation = operation;
                }
                else if (operation.getOperationType() == Operation.OperationType.AUTHORIZE) {
                    authorizeOperation = operation;
                }
            }

            if (authorizeOperation == null) {
                payment.setStatus(Payment.Status.ERROR);
                payment.setStatusMessage("Не найдена операция authorize");
                paymentRepository.update(payment);
                throw new RuntimeException(String.format("Не найдена операция authorize у платежа %s", payment));
            }

            if (clearingOperation == null) {
                payment.setStatus(Payment.Status.ERROR);
                payment.setStatusMessage("Не найдена операция clear");
                paymentRepository.update(payment);
                throw new RuntimeException(String.format("Не найдена операция clear у платежа %s", payment));
            }

            // Делаем запрос на clear в банк экваер
            clearingOperation.setRequestParams(String.format("orderId=%s, sum=%s", payment.getId(), authorizeOperation.getSum()));
            clearingOperation.setStatus(Operation.Status.SENT);
            clearingOperation.setSum(authorizeOperation.getSum());
            clearingOperation.setBankAcquireId(bankAcquireService.getBankId());
            operationRepository.update(clearingOperation);

            final BankAcquireResponse bankAcquireResponse = bankAcquireService.clear(payment.getPaymentN(), clearingOperation.getSum());

            switch (bankAcquireResponse.getOperationStatus()) {
                case SUCCESS: {
                    clearingOperation.setStatus(Operation.Status.SUCCESS);
                    payment.setStatus(Payment.Status.CLEAR);
                    payment.setStatusMessage("Платеж успешно завершен");
                    break;
                }
                case DECLINED: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    payment.setStatus(Payment.Status.DECLINED);
                    payment.setStatusMessage(String.format("Банк не разрешнил платеж, ответ: %s", bankAcquireResponse));
                    break;
                }
                case DO_NOT_HONOR: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    payment.setStatus(Payment.Status.DECLINED);
                    payment.setStatusMessage(String.format("Банк не разрешнил платеж, ответ: %s", bankAcquireResponse));
                    break;
                }
                case ERROR: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    payment.setStatus(Payment.Status.ERROR);
                    payment.setStatusMessage(String.format("Платеж завершился ошибкой на этапе клиринга: %s", bankAcquireResponse));
                    break;
                }
                case TOO_MANY_CONNECTIONS: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    payment.setStatus(Payment.Status.ERROR);
                    payment.setStatusMessage(String.format("Платеж завершился ошибкой на этапе клиринга: %s", bankAcquireResponse));
                    break;
                }
            }
            clearingOperation.setResponseParams(bankAcquireResponse.toString());
            payment.setChangedDate(new Date());
            LOG.info(String.format("Сохраним изменения в базе данных: %s", payment));
            operationRepository.update(clearingOperation);
            paymentRepository.update(payment);
        });
    }
}
