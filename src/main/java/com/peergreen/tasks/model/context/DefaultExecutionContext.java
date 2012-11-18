package com.peergreen.tasks.model.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public class DefaultExecutionContext implements ExecutionContext {
    private Map<String, Object> properties;
    private Collection<Object> objects;

    public DefaultExecutionContext() {
        this.properties = new ConcurrentHashMap<String, Object>();
        this.objects = new HashSet<Object>();
    }

    @Override
    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        Object value = getProperty(name);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        Object value = getProperty(name);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type, T defaultValue) {
        Object value = getProperty(name, defaultValue);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public void removeProperty(String name) {
        properties.remove(name);
    }

    @Override
    public <T> T get(Class<T> type) {
        List<T> candidates = new ArrayList<T>();
        for (Object object : objects) {
            if (type.isInstance(object)) {
                candidates.add(type.cast(object));
            }
        }

        if (candidates.isEmpty()) {
            return null;
        } else if (candidates.size() == 1) {
            return candidates.iterator().next();
        } else {
            // multiple candidates
            // TODO Need sorting to select the closer instance
            return candidates.iterator().next();
        }
    }

    @Override
    public void remove(Object instance) {
        objects.remove(instance);
    }

    @Override
    public void add(Object instance) {
        objects.add(instance);
    }
}
