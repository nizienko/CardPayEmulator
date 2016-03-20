package ru.yamoney.test.services.card_pay;

import ru.yamoney.test.repository.DataBaseEntity;

import java.util.Date;

/**
 * Created by def on 20.03.2016.
 */
public class Operation implements DataBaseEntity {
    private Integer id;
    private Integer orderId;
    private Integer operationType;
    private Integer status;
    private Integer bankAcquireId;
    private String requestParams;
    private String responseParams;
    private Date createdDate;
    private Date changedDate;
}
