package ru.yamoney.test.entity;

import ru.yamoney.test.services.card_pay.PayCardResult;

/**
 * Created by def on 20.03.2016.
 */
public class PayCardResponseEntity implements DomainObject {
    private String result;
    private String message;

    public PayCardResponseEntity(PayCardResult payCardResult) {
        this.result = payCardResult.getResultCode().name().toLowerCase();
        this.message = payCardResult.getMessage().getMessage();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
