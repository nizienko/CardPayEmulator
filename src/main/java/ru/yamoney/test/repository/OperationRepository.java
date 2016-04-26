package ru.yamoney.test.repository;

import java.util.List;

/**
 * Created by def on 27.03.2016.
 */
public interface OperationRepository <V extends DataBaseEntity> extends CommonRepository<V> {
    List<V> getByPaymentId(Integer id);
}
