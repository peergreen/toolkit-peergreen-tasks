package com.peergreen.tasks.model.context;

import com.peergreen.tasks.model.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionContext {
    private Map<Class<?>, Object> extensions;
    private Map<String, Object> properties;

    public ExecutionContext() {
        this.extensions = new ConcurrentHashMap<Class<?>, Object>();
        this.properties = new ConcurrentHashMap<String, Object>();
    }

    public Map<Class<?>, Object> getExtensions() {
        return extensions;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }


    public TaskContext newTaskContext(Breadcrumb breadcrumb, Task child) {
        return new DefaultTaskContext(this, new Breadcrumb(breadcrumb, child));
    }

}
