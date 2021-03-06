package ru.yamoney.test.services.card_pay;

/**
 * Created by nizienko on 19.03.2016.
 */
public class Card {
    private String cardNumber;
    private Integer month;
    private Integer year;
    private String holder;
    private String cvc;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        try {
            Integer.parseInt(cvc);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Что-то не так с CVC");
        }
        this.cvc = cvc;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", holder='" + holder + '\'' +
                ", cvc=" + cvc +
                '}';
    }
}
