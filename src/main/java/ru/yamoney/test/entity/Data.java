package ru.yamoney.test.entity;

import javax.persistence.Entity;
import java.util.UUID;

/**
 * Created by def on 13.02.16.
 */
public class Data implements DomainObject {
    private UUID id;
    private String description;

    public Data(UUID id, String description) {
        this.id = id;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
