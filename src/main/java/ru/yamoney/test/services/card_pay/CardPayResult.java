package ru.yamoney.test.services.card_pay;

import java.io.Serializable;

/**
 * Created by nizienko on 19.03.2016.
 */
public class CardPayResult implements Serializable {
    private Code resultCode;
    private Message message;

    public CardPayResult(Code resultCode, Message message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public CardPayResult(Code resultCode) {
        this.resultCode = resultCode;
    }

    public Code getResultCode() {
        return resultCode;
    }

    public void setResultCode(Code resultCode) {
        this.resultCode = resultCode;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static enum Code {
        SUCCESS(0), ERROR(1);

        private int code;

        Code(int code){
            this.code = code;
        }
    }

    public static enum Message {
        INVALID_CARD_NUMBER("Номер карты не валиден"),
        CARD_EXPIRED("Карта просрочена");

        private String message;

        Message(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
