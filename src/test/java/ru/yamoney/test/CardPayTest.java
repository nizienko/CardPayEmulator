package ru.yamoney.test;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.yamoney.test.services.card_pay.Card;
import ru.yamoney.test.services.card_pay.PayCardResult;
import ru.yamoney.test.services.card_pay.PayCardService;
import ru.yamoney.test.services.card_pay.PayCardServiceImpl;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by nizienko on 19.03.2016.
 */
public class CardPayTest {
    private static PayCardService payCardService;

    @BeforeClass
    public static void before(){
        payCardService = new PayCardServiceImpl();
    }

    @Test
    public void notValidCardNumber(){
        Card card = new Card();
        card.setCardNumber("4444434444444448");
        PayCardResult result = payCardService.payViaCard(card, BigDecimal.TEN, "Тестовый магазин");
        assertTrue(result.getResultCode() == PayCardResult.Code.ERROR);
        assertTrue(result.getMessage() == PayCardResult.Message.INVALID_CARD_NUMBER);
    }

    @Test
    public void successPay(){
        Card card = new Card();
        card.setCardNumber("4444444444444448");
        card.setMonth(9);
        card.setYear(2016);
        PayCardResult result = payCardService.payViaCard(card, BigDecimal.TEN, "Тестовый магазин");
        assertTrue(result.getResultCode() == PayCardResult.Code.SUCCESS);
    }
}
