package com.peergreen.tasks.model.context;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.editor.Reference;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTaskContext implements TaskContext {

    private Breadcrumb breadcrumb;
    private ExecutionContext executionContext;

    public DefaultTaskContext(ExecutionContext executionContext, Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
        this.executionContext = executionContext;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    @Override
    public <T extends Task> T find(Reference<T> reference) {
        return reference.resolve(breadcrumb);
    }

    @Override
    public Map<String, Object> getProperties() {
        return executionContext.getProperties();
    }

    @Override
    public <T> T get(Class<T> type) {
        return type.cast(executionContext.getExtensions().get(type));
    }

    @Override
    public <T> void remove(Class<T> type) {
        executionContext.getExtensions().remove(type);
    }

    @Override
    public <T> void add(Class<T> type, T instance) {
        executionContext.getExtensions().put(type, instance);
    }

}
