package com.peergreen.tasks.execution;

import com.peergreen.tasks.context.TaskContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 09/11/12
* Time: 14:44
* To change this template use File | Settings | File Templates.
*/
public interface ExecutionBuilder {
    Execution newExecution(TaskContext taskContext);
}
