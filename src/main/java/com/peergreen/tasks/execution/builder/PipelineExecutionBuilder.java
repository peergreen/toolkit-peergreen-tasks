package com.peergreen.tasks.execution.builder;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.execution.internal.PipelineExecution;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecutionBuilder implements ExecutionBuilder {

    private ExecutionBuilderManager executionBuilderManager;

    public PipelineExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        this.executionBuilderManager = executionBuilderManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext) {
        Task task = taskContext.getBreadcrumb().getCurrent();
        if (task instanceof Pipeline) {
            return new PipelineExecution(executionBuilderManager, taskContext, (Pipeline) task);
        }
        return null;
    }
}