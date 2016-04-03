package ru.yamoney.test.services.card_pay;




/**
 * Created by nizienko on 19.03.2016.
 */
public class PayCardResult {
    private Code resultCode;
    private Message message;

    public PayCardResult(Code resultCode, Message message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public PayCardResult() {
    }

    public PayCardResult(Code resultCode) {
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
        SUCCESS, DECLINED, ERROR;
    }

    public enum Message {
        SUCCESS("Платеж успешно завершен"),
        INVALID_CARD_NUMBER("Номер карты не валиден"),
        CARD_EXPIRED("Карта просрочена"),
        UNKNOWN_ERROR("Произошла ошибка"),
        DECLINED("Банк отклонил операцию");

        private String message;

        Message(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Override
    public String toString() {
        return "PayCardResult{" +
                "resultCode=" + resultCode +
                ", message=" + message +
                '}';
    }
}
