package com.peergreen.tasks.model.util;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.execution.RootExecution;
import com.peergreen.tasks.model.execution.builder.ParallelExecutionBuilder;
import com.peergreen.tasks.model.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.model.execution.builder.UnitOfWorkExecutionBuilder;
import com.peergreen.tasks.model.tracker.TrackerManager;
import com.peergreen.tasks.model.tracker.state.StateTaskTracker;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class Executions {
    public static RootExecution newRootExecution(ExecutorService executorService, Task task) {
        RootExecution root = new RootExecution(task);

        root.getTrackerManager().registerTracker(new StateTaskTracker());

        root.addExecutionBuilder(newUnitOfWorkExecutionBuilder(executorService, root.getTrackerManager()));
        root.addExecutionBuilder(newPipelineExecutionBuilder(root, root.getTrackerManager()));
        root.addExecutionBuilder(newParallelExecutionBuilder(root, root.getTrackerManager()));

        return root;
    }

    private static ExecutionBuilder newParallelExecutionBuilder(ExecutionBuilderManager executionBuilderManager, TrackerManager trackerManager) {
        return new ParallelExecutionBuilder(executionBuilderManager, trackerManager);
    }

    private static ExecutionBuilder newPipelineExecutionBuilder(ExecutionBuilderManager executionBuilderManager, TrackerManager trackerManager) {
        return new PipelineExecutionBuilder(executionBuilderManager, trackerManager);
    }

    private static ExecutionBuilder newUnitOfWorkExecutionBuilder(ExecutorService executorService, TrackerManager trackerManager) {
        return new UnitOfWorkExecutionBuilder(executorService, trackerManager);
    }
}
