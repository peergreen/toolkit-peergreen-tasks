package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.context.ExecutionContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutionBuilderManager {
    Execution newExecution(Breadcrumb breadcrumb, Task task);
}