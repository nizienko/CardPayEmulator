package ru.yamoney.test.services.card_pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yamoney.test.repository.OperationRepository;
import ru.yamoney.test.repository.OrderRepository;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireResponse;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireService;
import ru.yamoney.test.utils.CardUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static ru.yamoney.test.services.card_pay.PayCardResult.Code.DECLINED;
import static ru.yamoney.test.services.card_pay.PayCardResult.Code.ERROR;
import static ru.yamoney.test.services.card_pay.PayCardResult.Code.SUCCESS;
import static ru.yamoney.test.services.card_pay.PayCardResult.Message.CARD_EXPIRED;
import static ru.yamoney.test.services.card_pay.PayCardResult.Message.INVALID_CARD_NUMBER;
import static ru.yamoney.test.services.card_pay.PayCardResult.Message.UNKNOWN_ERROR;

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
    @Qualifier("orderRepository")
    private OrderRepository orderRepository;

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

        // Создадим новый приказ
        final Order order = new Order();
        order.generateOrderN();
        order.setCreatedDate(new Date());
        order.setStatus(Order.Status.CREATED);
        order.setStatusMessage("Запрос принят в обработку");
        LOG.info(String.format("Создан новый приказ: %s", order));

        // Сохраним приказ в БД
        orderRepository.insert(order);

        final Order createdOrder = (Order) orderRepository.fetchByOrderN(order.getOrderN());
        LOG.info(String.format("Приказ сохранен в базе данных: %s", createdOrder));


        // Создаем операцию
        final Operation operation = new Operation();
        operation.setOrderId(createdOrder.getId());
        operation.setSum(sum);
        operation.setOperationType(Operation.OperationType.AUTHORIZE);
        operation.setStatus(Operation.Status.CREATED);
        operation.setCreatedDate(new Date());

        operationRepository.insert(operation);
        final List<Operation> operations = operationRepository.getByOrderId(createdOrder.getId());
        Operation authorizeOperation = null;
        for (Operation o : operations) {
            if (o.getOperationType() == Operation.OperationType.AUTHORIZE) {
                authorizeOperation = o;
                break;
            }
        }
        if (authorizeOperation == null) {
            throw new RuntimeException(String.format("Не найдена операция authorize у приказа %s", createdOrder));
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
                createdOrder.setStatus(Order.Status.AUTHORIZE);
                payCardResult.setResultCode(SUCCESS);
                payCardResult.setMessage(PayCardResult.Message.SUCCESS);
                break;
            }
            case DECLINED: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdOrder.setStatus(Order.Status.DECLINED);
                payCardResult.setResultCode(DECLINED);
                payCardResult.setMessage(PayCardResult.Message.DECLINED);
                break;
            }
            case ERROR: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdOrder.setStatus(Order.Status.ERROR);
                payCardResult.setResultCode(ERROR);
                payCardResult.setMessage(PayCardResult.Message.UNKNOWN_ERROR);
                break;
            }
            case DO_NOT_HONOR: {
                authorizeOperation.setStatus(Operation.Status.ERROR);
                createdOrder.setStatus(Order.Status.DECLINED);
                payCardResult.setResultCode(DECLINED);
                payCardResult.setMessage(PayCardResult.Message.DECLINED);
                break;
            }
        }
        authorizeOperation.setResponseParams(bankAcquireResponse.toString());
        createdOrder.setStatusMessage(String.format("Ответ банка экваера: %s", bankAcquireResponse));
        createdOrder.setChangedDate(new Date());
        LOG.info(String.format("Сохраним изменения в базе данных: %s", createdOrder));
        operationRepository.update(authorizeOperation);
        orderRepository.update(createdOrder);
        LOG.info(String.format("Ответ: %s", payCardResult));
        return payCardResult;
    }
}
