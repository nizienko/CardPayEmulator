package ru.yamoney.test.repository.example;

import ru.yamoney.test.entity.DomainObject;

import java.util.Set;

/**
 * Created by def on 13.02.16.
 */
public interface DataRepository<V extends DomainObject> {
    void persist(V object);
    void delete(V object);
    Set<String> getRandomData();
}
