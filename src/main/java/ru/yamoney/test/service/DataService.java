package ru.yamoney.test.service;

import java.util.Set;

/**
 * Created by def on 13.02.16.
 */
public interface DataService {
    public boolean persist(String problem);

    public Set<String> getRandomData();
}
