package ru.yamoney.test.entity;

import ru.yamoney.test.entity.DomainObject;
import ru.yamoney.test.entity.Parameter;
import ru.yamoney.test.entity.ParameterValue;

/**
 * Created by def on 16.02.16.
 */
public class KeyValue<K, V> implements DomainObject {
    private K parameter;
    private V value;

    public KeyValue(K parameter, V value) {
        this.parameter = parameter;
        this.value = value;
    }

    public K getParameter() {
        return parameter;
    }

    public void setParameter(K parameter) {
        this.parameter = parameter;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}