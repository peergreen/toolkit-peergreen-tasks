package com.peergreen.tasks.model.execution.builder;

import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.execution.internal.DelegateExecution;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class DelegateExecutionBuilder implements ExecutionBuilder {

    private ExecutionBuilderManager executionBuilderManager;

    public DelegateExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        this.executionBuilderManager = executionBuilderManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext) {
        Task task = taskContext.getBreadcrumb().getCurrent();
        if (task instanceof Delegate) {
            return new DelegateExecution(executionBuilderManager, taskContext, (Delegate<?>) task);
        }
        return null;
    }
}
