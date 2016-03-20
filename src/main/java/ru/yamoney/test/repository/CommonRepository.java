package ru.yamoney.test.repository;

import ru.yamoney.test.entity.DomainObject;

import java.util.List;

/**
 * Created by def on 13.02.16.
 */
public interface CommonRepository<V extends DataBaseEntity> {
    void insert(V data);
    void update(V data);
    void delete(V data);
    List<V> fetch();
    V fetchById(long id);
}
