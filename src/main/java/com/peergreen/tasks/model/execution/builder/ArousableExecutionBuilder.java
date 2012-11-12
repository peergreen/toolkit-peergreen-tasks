package com.peergreen.tasks.model.execution.builder;

import com.peergreen.tasks.model.ArousableTask;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.execution.internal.ArousableExecution;
import com.peergreen.tasks.model.execution.internal.ParallelExecution;
import com.peergreen.tasks.model.tracker.TrackerManager;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class ArousableExecutionBuilder implements ExecutionBuilder {

    private ExecutionBuilderManager executionBuilderManager;
    private TrackerManager trackerManager;

    public ArousableExecutionBuilder(ExecutionBuilderManager executionBuilderManager, TrackerManager trackerManager) {
        this.executionBuilderManager = executionBuilderManager;
        this.trackerManager = trackerManager;
    }

    @Override
    public Execution newExecution(Task task) {
        if (task instanceof ArousableTask) {
            return new ArousableExecution(trackerManager, executionBuilderManager, (ArousableTask) task);
        }
        return null;
    }
}
