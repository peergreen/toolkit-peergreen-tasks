package com.peergreen.tasks.model.group;

import com.peergreen.tasks.model.context.DefaultExecutionContext;
import com.peergreen.tasks.model.context.ExecutionContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 19/11/12
* Time: 11:17
* To change this template use File | Settings | File Templates.
*/
class MutableExecutionContext implements ExecutionContext {
    private ExecutionContext delegate;
    private ExecutionContext local = new DefaultExecutionContext();

    public void setDelegate(ExecutionContext delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setProperty(String name, Object value) {
        local.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) {
        Object value = local.getProperty(name);
        if (value == null) {
            value = delegate.getProperty(name);
        }
        return value;
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        Object value = local.getProperty(name);
        if (value == null) {
            value = delegate.getProperty(name, defaultValue);
        }
        return value;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        T value = local.getProperty(name, type);
        if (value == null) {
            value = delegate.getProperty(name, type);
        }
        return value;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type, T defaultValue) {
        T value = local.getProperty(name, type);
        if (value == null) {
            value = delegate.getProperty(name, type, defaultValue);
        }
        return value;
    }

    @Override
    public void removeProperty(String name) {
        local.removeProperty(name);
    }

    @Override
    public <T> T get(Class<T> type) {
        T value = local.get(type);
        if (value == null) {
            value = delegate.get(type);
        }
        return value;
    }

    @Override
    public void remove(Object instance) {
        local.remove(instance);
    }

    @Override
    public void add(Object instance) {
        local.add(instance);
    }
}
