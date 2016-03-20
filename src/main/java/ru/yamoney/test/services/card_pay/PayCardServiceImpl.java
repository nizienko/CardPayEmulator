package ru.yamoney.test.services.card_pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yamoney.test.repository.OrderRepository;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireResponse;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireService;
import ru.yamoney.test.utils.CardUtils;

import java.math.BigDecimal;
import java.util.Date;

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

    @Override
    public PayCardResult payViaCard(Card card, BigDecimal sum, String shop) {
        // Проверим валидность номера карты
        if(!CardUtils.isCardNumberValid(card.getCardNumber())) {
            LOG.error(INVALID_CARD_NUMBER.getMessage());
            return new PayCardResult(ERROR, INVALID_CARD_NUMBER);
        }
        // Проверим, что карта не протухла
        if (CardUtils.isCardExpired(card.getMonth(), card.getYear())){
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
        LOG.info(String.format("Приказ %s сохранен в базе данных", order.getOrderN()));

        final Order createdOrder = (Order) orderRepository.fetchByOrderN(order.getOrderN());
        LOG.info(String.format("Сохраненный приказ: %s", createdOrder));

        // Делаем запрос на authorize в банк экваер
        final BankAcquireResponse bankAcquireResponse = bankAcquireService.authorize(card, sum);

        // Ответим в зависимости от ответа экваера
        switch (bankAcquireResponse.getOperationStatus()) {
            case SUCCESS: {
                return new PayCardResult(SUCCESS, PayCardResult.Message.SUCCESS);
            }
            case DECLINED: {
                return new PayCardResult(DECLINED, PayCardResult.Message.DECLINED);
            }
        }
        return new PayCardResult(ERROR, UNKNOWN_ERROR);
    }
}
