package ru.yamoney.test.entity;

/**
 * Created by def on 15.02.16.
 */
public class ParameterValue implements DomainObject{
    private long parameterId;
    private long instanceId;
    private String value;

    public ParameterValue(long parameterId, long instanceId, String value) {
        this.parameterId = parameterId;
        this.instanceId = instanceId;
        this.value = value;
    }

    public long getParameterId() {
        return parameterId;
    }

    public void setParameterId(long parameterId) {
        this.parameterId = parameterId;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
