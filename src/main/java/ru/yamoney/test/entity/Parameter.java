package ru.yamoney.test.entity;

/**
 * Created by def on 14.02.16.
 */
public class Parameter implements DomainObject {
    private long id;
    private long groupId;
    private String name;
    private String description;

    public Parameter(long id, long groupId, String name, String description) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    public Parameter(long groupId, String name, String description) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
