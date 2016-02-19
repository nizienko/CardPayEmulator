package ru.yamoney.test.repository.test_settings.value;

import ru.yamoney.test.entity.DomainObject;
import ru.yamoney.test.repository.test_settings.FetchByInstanceRepository;

/**
 * Created by def on 18.02.16.
 */
public interface ValueRepository<V extends DomainObject> extends FetchByInstanceRepository<V> {
    V fetchById(long instanceId, long parameterId);
}
