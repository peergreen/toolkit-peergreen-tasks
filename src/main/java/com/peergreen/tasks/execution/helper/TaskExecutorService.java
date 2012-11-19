package com.peergreen.tasks.execution.helper;

import com.peergreen.tasks.context.DefaultExecutionContext;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.execution.helper.DefaultExecutionBuilderManager;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class TaskExecutorService {

    private ExecutionBuilderManager executionBuilderManager;
    private ExecutionContext executionContext = new DefaultExecutionContext();

    public TaskExecutorService(ExecutionBuilderManager executionBuilderManager) {
        this.executionBuilderManager = executionBuilderManager;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void execute(Task task) {
        execute(executionContext, task);
    }

    public void execute(ExecutionContext executionContext, Task task) {
        executionBuilderManager.newExecution(executionContext, null, task)
                .execute();
    }

}
