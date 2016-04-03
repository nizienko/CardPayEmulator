package ru.yamoney.test.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.yamoney.test.entity.PayCardResponseEntity;
import ru.yamoney.test.services.card_pay.Card;
import ru.yamoney.test.services.card_pay.PayCardResult;
import ru.yamoney.test.services.card_pay.PayCardService;
import ru.yamoney.test.utils.Ajax;
import ru.yamoney.test.utils.RestException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by def on 20.03.2016.
 */

@Controller
public class PayCardController {
    private static final Logger LOG = Logger.getLogger(PayCardController.class);

    @Autowired
    @Qualifier("PayCardServiceImpl")
    private PayCardService payCardService;

    @RequestMapping(value = "/card/pay", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> payCard(
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year,
            @RequestParam("holder") String holder,
            @RequestParam("cvc") String cvc,
            @RequestParam("sum") String sum
    ) throws RestException {
        try {
            final Card card = new Card();
            card.setCardNumber(cardNumber);
            card.setMonth(month);
            card.setYear(year);
            card.setHolder(holder);
            card.setCvc(cvc);
            final BigDecimal amount = new BigDecimal(sum);
            final PayCardResult payCardResult = payCardService.payViaCard(card, amount, "Тестовый магазин");
            return Ajax.successResponse(new PayCardResponseEntity(payCardResult));
        } catch (Exception e) {
//            e.printStackTrace();
//            return Ajax.errorResponse(e.getMessage());
            throw new RestException(e);
        }
    }

}
