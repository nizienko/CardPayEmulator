package ru.yamoney.test.services.card_pay;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yamoney.test.repository.OperationRepository;
import ru.yamoney.test.repository.OrderRepository;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireResponse;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ru.yamoney.test.services.card_pay.PayCardResult.Code.DECLINED;
import static ru.yamoney.test.services.card_pay.PayCardResult.Code.SUCCESS;

/**
 * Created by nizienko on 03.04.2016.
 */

@Service("ClearingServiceImpl")
@EnableScheduling
public class ClearingServiceImpl implements ClearingService {
    private static final Logger LOG = LoggerFactory.getLogger(ClearingServiceImpl.class);

    @Autowired
    @Qualifier("orderRepository")
    private OrderRepository orderRepository;

    @Autowired
    @Qualifier("operationRepository")
    private OperationRepository operationRepository;

    @Autowired
    @Qualifier("moscowBankAcquireService")
    private BankAcquireService bankAcquireService;

    @Scheduled(fixedDelay = 10000)
    public void clearPayments() {
        for (Object o : orderRepository.fetchForClearing()) {
            final Order order = (Order) o;
            LOG.info(String.format("Найден приказ для которого требуется провести клиринг: %s", order));
            List<Operation> operations = operationRepository.getByOrderId(order.getId());
            LOG.info(String.format("В приказе %s сущесвтуют операции: %s", order.getId(), operations));

            LOG.info("Создаем новую операцию клиринга");

            Operation clearingOperation = new Operation();
            clearingOperation.setOrderId(order.getId());
            clearingOperation.setOperationType(Operation.OperationType.CLEAR);
            clearingOperation.setStatus(Operation.Status.CREATED);
            clearingOperation.setCreatedDate(new Date());

            operationRepository.insert(clearingOperation);

            operations = operationRepository.getByOrderId(order.getId());

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
                order.setStatus(Order.Status.ERROR);
                order.setStatusMessage("Не найдена операция authorize");
                orderRepository.update(order);
                throw new RuntimeException(String.format("Не найдена операция authorize у приказа %s", order));
            }

            if (clearingOperation == null) {
                order.setStatus(Order.Status.ERROR);
                order.setStatusMessage("Не найдена операция clear");
                orderRepository.update(order);
                throw new RuntimeException(String.format("Не найдена операция clear у приказа %s", order));
            }

            // Делаем запрос на clear в банк экваер
            clearingOperation.setRequestParams(String.format("orderId=%s, sum=%s", order.getId(), authorizeOperation.getSum()));
            clearingOperation.setStatus(Operation.Status.SENT);
            clearingOperation.setSum(authorizeOperation.getSum());
            clearingOperation.setBankAcquireId(bankAcquireService.getBankId());
            operationRepository.update(clearingOperation);

            final BankAcquireResponse bankAcquireResponse = bankAcquireService.clear(order.getOrderN(), clearingOperation.getSum());

            switch (bankAcquireResponse.getOperationStatus()) {
                case SUCCESS: {
                    clearingOperation.setStatus(Operation.Status.SUCCESS);
                    order.setStatus(Order.Status.CLEAR);
                    order.setStatusMessage("Платеж успешно завершен");
                    break;
                }
                case DECLINED: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    order.setStatus(Order.Status.DECLINED);
                    order.setStatusMessage(String.format("Банк не разрешнил платеж, ответ: %s", bankAcquireResponse));
                    break;
                }
                case DO_NOT_HONOR: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    order.setStatus(Order.Status.DECLINED);
                    order.setStatusMessage(String.format("Банк не разрешнил платеж, ответ: %s", bankAcquireResponse));
                    break;
                }
                case ERROR: {
                    clearingOperation.setStatus(Operation.Status.ERROR);
                    order.setStatus(Order.Status.ERROR);
                    order.setStatusMessage(String.format("Платеж завершился ошибкой на этапе клиринга: %s", bankAcquireResponse));
                    break;
                }
            }
            clearingOperation.setResponseParams(bankAcquireResponse.toString());
            order.setChangedDate(new Date());
            LOG.info(String.format("Сохраним изменения в базе данных: %s", order));
            operationRepository.update(clearingOperation);
            orderRepository.update(order);
        }
    }
}

