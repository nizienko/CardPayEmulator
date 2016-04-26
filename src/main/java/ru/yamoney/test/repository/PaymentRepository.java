package ru.yamoney.test.repository;

import java.util.List;

/**
 * Created by def on 20.03.2016.
 */
public interface PaymentRepository<V extends DataBaseEntity> extends CommonRepository<V> {
    V fetchByOrderN(String orderN);
    List<V> fetchForClearing();
}
