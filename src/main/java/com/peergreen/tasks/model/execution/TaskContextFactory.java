package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.context.ExecutionContext;
import com.peergreen.tasks.model.context.TaskContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/11/12
 * Time: 19:19
 * To change this template use File | Settings | File Templates.
 */
public interface TaskContextFactory {
    TaskContext createTaskContext(ExecutionContext parent, Breadcrumb breadcrumb, Task task);
}
