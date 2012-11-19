package com.peergreen.tasks.context;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.execution.TaskContextFactory;

public class DefaultTaskContextFactory implements TaskContextFactory {
    @Override
    public TaskContext createTaskContext(ExecutionContext parent, Breadcrumb breadcrumb, Task task) {
        return new DefaultTaskContext(parent, new Breadcrumb(breadcrumb, task));
    }
}