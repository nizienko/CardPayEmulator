package ru.yamoney.test.repository;

/**
 * Created by def on 20.03.2016.
 */
public interface OrderRepository<V extends DataBaseEntity> extends CommonRepository<V> {
    V fetchByOrderN(String orderN);

}
