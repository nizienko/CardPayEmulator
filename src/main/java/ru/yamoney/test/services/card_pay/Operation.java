package ru.yamoney.test.services.card_pay;

import ru.yamoney.test.repository.DataBaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by def on 20.03.2016.
 */
public class Operation implements DataBaseEntity {
    private Integer id;
    private Integer paymentId;
    private OperationType operationType;
    private Status status;
    private BigDecimal sum;
    private Integer bankAcquireId;
    private String requestParams;
    private String responseParams;
    private Date createdDate;
    private Date changedDate;

    public enum OperationType {
        AUTHORIZE(2), CLEAR(3), CANCEL(15);

        private int operationType;

        OperationType(int operationType) {
            this.operationType = operationType;
        }

        public int getCode() {
            return operationType;
        }

        public static OperationType getOperationType(Integer code) {
            switch (code) {
                case 2 : return AUTHORIZE;
                case 3 : return CLEAR;
                case 15 : return CANCEL;
            }
            throw new IllegalArgumentException(String.format("Неизвестный код типа операции %s", code));
        }
    }

    public enum Status {
        CREATED(0), SENT(5), SUCCESS(1), ERROR(10);

        private int status;

        Status(int status){
            this.status = status;
        }

        public int getCode() {
            return status;
        }

        public static Status getStatus(Integer code) {
            switch (code) {
                case 0 : return CREATED;
                case 5 : return SENT;
                case 1 : return SUCCESS;
                case 10 : return ERROR;
            }
            throw new IllegalArgumentException(String.format("Неизвестный статус операции %s", code));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getBankAcquireId() {
        return bankAcquireId;
    }

    public void setBankAcquireId(Integer bankAcquireId) {
        this.bankAcquireId = bankAcquireId;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(String responseParams) {
        this.responseParams = responseParams;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", paymentId=" + paymentId +
                ", operationType=" + operationType +
                ", status=" + status +
                ", sum=" + sum +
                ", bankAcquireId=" + bankAcquireId +
                ", requestParams='" + requestParams + '\'' +
                ", responseParams='" + responseParams + '\'' +
                ", createdDate=" + createdDate +
                ", changedDate=" + changedDate +
                '}';
    }
}
