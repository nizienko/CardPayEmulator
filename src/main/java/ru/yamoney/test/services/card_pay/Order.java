package ru.yamoney.test.services.card_pay;

import ru.yamoney.test.repository.DataBaseEntity;

import java.util.Date;

/**
 * Created by def on 20.03.2016.
 */
public class Order implements DataBaseEntity {
    private Integer id;
    private String orderN;
    private Date createdDate;
    private Date changedDate;
    private Status status;
    private String statusMessage;

    public static enum Status {
        CREATED(1), AUTHORIZE(2), CLEAR(3), ERROR(10);

        private int status;

        Status(int status){
            this.status = status;
        }

        public int getCode() {
            return status;
        }

        public static Status getStatus(Integer code) {
            switch (code) {
                case 1 : return CREATED;
                case 2 : return AUTHORIZE;
                case 3 : return CLEAR;
                case 10 : return ERROR;
                default: return ERROR;
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getOrderN() {
        return orderN;
    }

    public void setOrderN(String orderN) {
        this.orderN = orderN;
    }

    public void generateOrderN() {
        orderN = "order_" + System.currentTimeMillis() ;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderN='" + orderN + '\'' +
                ", createdDate=" + createdDate +
                ", changedDate=" + changedDate +
                ", status=" + status +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
