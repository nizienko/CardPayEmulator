package ru.yamoney.test.services.card_pay.acquiring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yamoney.test.services.card_pay.Card;
import ru.yamoney.test.services.card_pay.PayCardServiceImpl;
import ru.yamoney.test.services.card_pay.acquiring.BankAcquireResponse.OperationStatus;

import java.math.BigDecimal;

/**
 * Created by def on 20.03.2016.
 */

@Service("moscowBankAcquireService")
public class MoscowBankAcquireService implements BankAcquireService {
    private static final Logger LOG = LoggerFactory.getLogger(BankAcquireService.class);

    @Override
    public BankAcquireResponse authorize(Card card, BigDecimal sum) {
        final BankAcquireResponse bankAcquireResponse = new BankAcquireResponse();
        LOG.info(String.format("Отправляем запрос на авторизацию операции на сумму %s в банк экваер: %s", sum, card));

        if (card.getCvc().equals("003")) {
            bankAcquireResponse.setOperationStatus(OperationStatus.DECLINED);
        }
        else if (card.getCvc().equals("005")) {
            bankAcquireResponse.setOperationStatus(OperationStatus.ERROR);
        }
        else if (card.getCvc().equals("007")) {
            bankAcquireResponse.setOperationStatus(OperationStatus.DO_NOT_HONOR);
        }
        else {
            bankAcquireResponse.setOperationStatus(OperationStatus.SUCCESS);
        }
        delay();
        LOG.info("Получен ответ от банка экваера: " + bankAcquireResponse);
        return bankAcquireResponse;
    }

    @Override
    public BankAcquireResponse clear(String operationId, BigDecimal sum) {
        final BankAcquireResponse bankAcquireResponse = new BankAcquireResponse();
        LOG.info(String.format("Отправляем запрос на клиринг операции %s на сумму %s в банк экваер", operationId, sum));

        if (sum.compareTo(BigDecimal.TEN) == 0) {
            bankAcquireResponse.setOperationStatus(OperationStatus.DECLINED);
        }
        else if (sum.compareTo(new BigDecimal("12")) == 0) {
            bankAcquireResponse.setOperationStatus(OperationStatus.ERROR);
        }
        else if (sum.compareTo(new BigDecimal("14")) == 0) {
            bankAcquireResponse.setOperationStatus(OperationStatus.DO_NOT_HONOR);
        }
        else {
            bankAcquireResponse.setOperationStatus(OperationStatus.SUCCESS);
        }
        delay();
        LOG.info("Получен ответ от банка экваера: " + bankAcquireResponse);
        return bankAcquireResponse;
    }

    /*
        Искуственная задержка
     */
    private void delay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Integer getBankId(){
        return 7;
    }
}
