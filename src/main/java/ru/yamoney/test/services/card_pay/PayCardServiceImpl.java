package ru.yamoney.test.services.card_pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yamoney.test.repository.OperationRepository;
import ru.yamoney.test.repository.PaymentRepository;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireResponse;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireService;
import ru.yamoney.test.utils.CardUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static ru.yamoney.test.services.card_pay.PayCardResult.Code.DECLINED;
import static ru.yamoney.test.services.card_pay.PayCardResult.Code.ERROR;
import static ru.yamoney.test.services.card_pay.PayCardResult.Code.SUCCESS;
import static ru.yamoney.test.services.card_pay.PayCardResult.Message.*;

/**
 * Created by nizienko on 19.03.2016.
 */
@Service("PayCardServiceImpl")
public class PayCardServiceImpl implements PayCardService {
    private static final Logger LOG = LoggerFactory.getLogger(PayCardServiceImpl.class);

    @Autowired
    @Qualifier("moscowBankAcquireService")
    private BankAcquireService bankAcquireService;

    @Autowired
    @Qualifier("paymentRepository")
    private PaymentRepository paymentRepository;

    @Autowired
    @Qualifier("operationRepository")
    private OperationRepository operationRepository;

    @Override
    public PayCardResult payViaCard(Card card, BigDecimal sum, String shop) {
        LOG.info(String.format("Получен запрос на платеж с параметрами: Карта %s, Сумма %s, Магазин %s", card, sum, shop));
        // Проверим валидность номера карты
        if(!CardUtils.isCardNumberValid(card.getCardNumber()) || card.getCardNumber().length() < 14 || card.getCardNumber().length() > 19) {
            LOG.error(INVALID_CARD_NUMBER.getMessage());
            return new PayCardResult(ERROR, INVALID_CARD_NUMBER);
        }
        // Проверим, что карта не протухла
        if (CardUtils.isCardExpired(card.getMonth(), card.getYear())){
            LOG.error(CARD_EXPIRED.getMessage());
            return new PayCardResult(ERROR, CARD_EXPIRED);
        }

        if (card.getCvc().length() != 3 && card.getCvc().length() != 4) {
            return new PayCardResult(ERROR, BAD_CVC);
        }

        if (sum.compareTo(BigDecimal.ZERO) <= 0 || sum.compareTo(new BigDecimal("700000")) > 0) {
            return new PayCardResult(ERROR, BAD_SUM);
        }

        // Создадим новый платеж
        final Payment payment = new Payment();
        payment.generatePaymentN();
        payment.setCreatedDate(new Date());
        payment.setStatus(Payment.Status.CREATED);
        payment.setStatusMessage("Запрос принят в обработку");
        LOG.info(String.format("Создан новый платеж: %s", payment));

        // Сохраним приказ в БД
        paymentRepository.insert(payment);

        final Payment createdPayment = (Payment) paymentRepository.fetchByOrderN(payment.getPaymentN());
        LOG.info(String.format("Платеж сохранен в базе данных: %s", createdPayment));


        // Создаем операцию
        final Operation operation = new Operation();
        operation.setPaymentId(createdPayment.getId());
        operation.setSum(sum);
        operation.setOperationType(Operation.OperationType.AUTHORIZE);
        operation.setStatus(Operation.Status.CREATED);
        operation.setCreatedDate(new Date());

        operationRepository.insert(operation);
        final List<Operation> operations = operationRepository.getByPaymentId(createdPayment.getId());
        Operation authorizeOperation = null;
        for (Operation o : operations) {
            if (o.getOperationType() == Operation.OperationType.AUTHORIZE) {
                authorizeOperation = o;
                break;
            }
        }
        if (authorizeOperation == null) {
            throw new RuntimeException(String.format("Не найдена операция authorize у платежа %s", createdPayment));
        }

        // Делаем запрос на authorize в банк экваер
        authorizeOperation.setRequestParams(String.format("card=%s, sum=%s", card, sum));
        authorizeOperation.setStatus(Operation.Status.SENT);
        authorizeOperation.setBankAcquireId(bankAcquireService.getBankId());
        operationRepository.update(authorizeOperation);

        final BankAcquireResponse bankAcquireResponse = bankAcquireService.authorize(card, sum);

        final PayCardResult payCardResult = new PayCardResult();
        switch (bankAcquireResponse.getOperationStatus()) {
            case SUCCESS: {
                authorizeOperation.setStatus(Operation.Status.SUCCESS);
                createdPayment.setStatus(Payment.Status.AUTHORIZE);
                payCardResult.setResultCode(SUCCESS);
                payCardResult.setMessage(PayCardResult.Message.SUCCESS);
                break;
            }
            case DECLINED: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdPayment.setStatus(Payment.Status.DECLINED);
                payCardResult.setResultCode(DECLINED);
                payCardResult.setMessage(PayCardResult.Message.DECLINED);
                break;
            }
            case ERROR: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdPayment.setStatus(Payment.Status.ERROR);
                payCardResult.setResultCode(ERROR);
                payCardResult.setMessage(PayCardResult.Message.UNKNOWN_ERROR);
                break;
            }
            case DO_NOT_HONOR: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdPayment.setStatus(Payment.Status.DECLINED);
                payCardResult.setResultCode(DECLINED);
                payCardResult.setMessage(PayCardResult.Message.DECLINED);
                break;
            }
            case TOO_MANY_CONNECTIONS: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdPayment.setStatus(Payment.Status.ERROR);
                payCardResult.setResultCode(ERROR);
                payCardResult.setMessage(PayCardResult.Message.BANK_BOTTLENECK_REACHED);
                break;
            }
        }
        authorizeOperation.setResponseParams(bankAcquireResponse.toString());
        createdPayment.setStatusMessage(String.format("Ответ банка экваера: %s", bankAcquireResponse));
        createdPayment.setChangedDate(new Date());
        LOG.info(String.format("Сохраним изменения в базе данных: %s", createdPayment));
        operationRepository.update(authorizeOperation);
        paymentRepository.update(createdPayment);
        LOG.info(String.format("Отвечаем верстке: %s", payCardResult));
        return payCardResult;
    }
}
