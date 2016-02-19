package ru.yamoney.test.repository.test_settings;

import ru.yamoney.test.entity.DomainObject;
import ru.yamoney.test.repository.CommonRepository;

import java.util.List;

/**
 * Created by def on 14.02.16.
 */
public interface FetchByGroupRepository<V extends DomainObject> extends CommonRepository<V> {
    List<V> fetchByGroupId(long groupId);
}
