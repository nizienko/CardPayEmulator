package ru.yamoney.test.services.card_pay.acquiring;


/**
 * Created by def on 20.03.2016.
 */
public class BankAcquireResponse {
    private OperationType operationType;
    private OperationStatus operationStatus;

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }
    public static enum OperationStatus {
        SUCCESS, ERROR, DECLINED, DO_NOT_HONOR, TOO_MANY_CONNECTIONS
    }

    public static enum OperationType {
        AUTHORIZE,
        CLEAR
    }

    @Override
    public String toString() {
        return "BankAcquireResponse{" +
                "operationType=" + operationType +
                ", operationStatus=" + operationStatus +
                '}';
    }
}
