package ru.yamoney.test.services.card_pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yamoney.test.utils.CardUtils;

import java.math.BigDecimal;

import static ru.yamoney.test.services.card_pay.CardPayResult.Code.ERROR;
import static ru.yamoney.test.services.card_pay.CardPayResult.Code.SUCCESS;
import static ru.yamoney.test.services.card_pay.CardPayResult.Message.CARD_EXPIRED;
import static ru.yamoney.test.services.card_pay.CardPayResult.Message.INVALID_CARD_NUMBER;

/**
 * Created by nizienko on 19.03.2016.
 */
@Service("CardPayServiceImpl")
public class CardPayServiceImpl implements CardPayService {
    private static final Logger LOG = LoggerFactory.getLogger(CardPayServiceImpl.class);


    @Override
    public CardPayResult payViaCard(Card card, BigDecimal sum, String shop) {
        // Проверим валидность номера карты
        if(!CardUtils.isCardNumberValid(card.getCardNumber())) {
            LOG.error(INVALID_CARD_NUMBER.getMessage());
            return new CardPayResult(ERROR, INVALID_CARD_NUMBER);
        }
        if (CardUtils.isCardExpired(card.getMonth(), card.getYear())){
            return new CardPayResult(ERROR, CARD_EXPIRED);
        }
        return new CardPayResult(SUCCESS);
    }
}
