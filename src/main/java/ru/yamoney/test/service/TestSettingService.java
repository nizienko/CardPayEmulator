package ru.yamoney.test.service;

import ru.yamoney.test.entity.GroupInstance;
import ru.yamoney.test.entity.KeyValue;

import java.util.List;

/**
 * Created by def on 14.02.16.
 */
public interface TestSettingService {
    List getGroups();
    void addGroup(String groupName, String description);
    List<GroupInstance> getGroupInstances(long groupId);
    void addGroupInstance(long groupId, String name, String description);
    List<KeyValue> getParametersMap(long instanceId);
    List<KeyValue> getParametersMap(long instanceId, String filter);
    void setParameter(long instanceId, long parameterId, String value);
    void addParameter(long groupId, String name, String description);
}
