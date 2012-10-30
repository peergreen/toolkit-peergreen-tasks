package com.peergreen.tasks.model.execution.builder;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.execution.internal.PipelineExecution;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecutionBuilder implements ExecutionBuilder {

    private ExecutionBuilderManager executionBuilderManager;
    private TrackerManager trackerManager;

    public PipelineExecutionBuilder(ExecutionBuilderManager executionBuilderManager, TrackerManager trackerManager) {
        this.executionBuilderManager = executionBuilderManager;
        this.trackerManager = trackerManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext, Task task) {
        if (task instanceof Pipeline) {
            return new PipelineExecution(trackerManager, executionBuilderManager, taskContext, (Pipeline) task);
        }
        return null;
    }
}
