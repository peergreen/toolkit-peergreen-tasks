package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Task;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 09/11/12
* Time: 14:44
* To change this template use File | Settings | File Templates.
*/
public interface ExecutionBuilder {
    Execution newExecution(Task task);
}
