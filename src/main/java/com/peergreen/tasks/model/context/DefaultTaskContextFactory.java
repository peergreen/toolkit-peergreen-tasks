package com.peergreen.tasks.model.context;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.TaskContextFactory;

public class DefaultTaskContextFactory implements TaskContextFactory {
    @Override
    public TaskContext createTaskContext(ExecutionContext parent, Breadcrumb breadcrumb, Task task) {
        return new DefaultTaskContext(parent, new Breadcrumb(breadcrumb, task));
    }
}