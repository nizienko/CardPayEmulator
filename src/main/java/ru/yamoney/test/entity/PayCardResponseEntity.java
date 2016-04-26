package ru.yamoney.test.entity;

import ru.yamoney.test.services.card_pay.PayCardResult;

/**
 * Created by def on 20.03.2016.
 */
public class PayCardResponseEntity implements DomainObject {
    private String status;
    private String message;

    public PayCardResponseEntity(PayCardResult payCardResult) {
        this.status = payCardResult.getResultCode().name().toLowerCase();
        this.message = payCardResult.getMessage().getMessage();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
