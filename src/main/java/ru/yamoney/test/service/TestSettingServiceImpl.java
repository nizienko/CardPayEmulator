package ru.yamoney.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yamoney.test.entity.*;
import ru.yamoney.test.repository.CommonRepository;
import ru.yamoney.test.repository.test_settings.FetchByGroupRepository;
import ru.yamoney.test.repository.test_settings.FetchByInstanceRepository;
import ru.yamoney.test.repository.test_settings.value.ValueRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by def on 14.02.16.
 */
@Service("testSettingService")
public class TestSettingServiceImpl implements TestSettingService {
    private static final Logger LOG = LoggerFactory.getLogger(TestSettingServiceImpl.class);

    @Autowired
    @Qualifier("groupRepository")
    private CommonRepository groupRepository;

    @Autowired
    @Qualifier("groupInstanceRepository")
    private FetchByGroupRepository groupInstanceRepository;

    @Autowired
    @Qualifier("parameterRepository")
    private FetchByGroupRepository parameterRepository;

    @Autowired
    @Qualifier("valueRepository")
    private ValueRepository valueRepository;

    @Override
    public List getGroups() {
        return groupRepository.fetch();
    }

    @Override
    public void addGroup(String groupName, String description) {
        groupRepository.insert(new Group(groupName, description));
    }

    @Override
    public List getGroupInstances(long groupId) {
        return groupInstanceRepository.fetchByGroupId(groupId);
    }

    @Override
    public void addGroupInstance(long groupId, String name, String description) {
        groupInstanceRepository.insert(new GroupInstance(groupId, name, description));
    }

    @Override
    public List<KeyValue> getParametersMap(long instanceId, String filter) {
        GroupInstance instance = (GroupInstance) groupInstanceRepository.fetchById(instanceId);
        List<Parameter> parameters = parameterRepository.fetchByGroupId(instance.getGroupId());
        List<ParameterValue> values = valueRepository.fetchByInstanceId(instanceId);
        Map<Long, ParameterValue> valuesMap = new HashMap<>();
        values.stream().forEach((ParameterValue p) -> valuesMap.put(p.getParameterId(), p));
        List<KeyValue> result = new ArrayList<>();
        parameters.stream()
                .filter((Parameter p) -> filter == null || p.getName().contains(filter))
                .forEach((Parameter p) -> result.add(new KeyValue<>(p, valuesMap.get(p.getId()))));
        return result;
    }

    @Override
    public List<KeyValue> getParametersMap(long instanceId) {
        return getParametersMap(instanceId, null);
    }

    @Override
    public void setParameter(long instanceId, long parameterId, String value) {
        try {
            ParameterValue parameterValue = (ParameterValue) valueRepository.fetchById(instanceId, parameterId);
            parameterValue.setValue(value);
            valueRepository.update(parameterValue);
        } catch (EmptyResultDataAccessException e) {
            ParameterValue parameterValue = new ParameterValue(parameterId, instanceId, value);
            valueRepository.insert(parameterValue);
        }
    }

    @Override
    public void addParameter(long groupId, String name, String description) {
        parameterRepository.insert(new Parameter(groupId, name, description));
    }
}

