package ru.yamoney.test;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yamoney.test.services.card_pay.Card;
import ru.yamoney.test.services.card_pay.CardPayResult;
import ru.yamoney.test.services.card_pay.CardPayService;
import ru.yamoney.test.services.card_pay.CardPayServiceImpl;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by nizienko on 19.03.2016.
 */
public class CardPayTest {
    private static CardPayService cardPayService;

    @BeforeClass
    public static void before(){
        cardPayService = new CardPayServiceImpl();
    }

    @Test
    public void notValidCardNumber(){
        Card card = new Card();
        card.setCardNumber("4444434444444448");
        CardPayResult result = cardPayService.payViaCard(card, BigDecimal.TEN, "Тестовый магазин");
        assertTrue(result.getResultCode() == CardPayResult.Code.ERROR);
        assertTrue(result.getMessage() == CardPayResult.Message.INVALID_CARD_NUMBER);
    }

    @Test
    public void successPay(){
        Card card = new Card();
        card.setCardNumber("4444444444444448");
        card.setMonth(04);
        card.setYear(2016);
        CardPayResult result = cardPayService.payViaCard(card, BigDecimal.TEN, "Тестовый магазин");
        assertTrue(result.getResultCode() == CardPayResult.Code.SUCCESS);
    }


}
