package com.peergreen.tasks.model.util;

import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.execution.RootExecution;
import com.peergreen.tasks.execution.builder.DelegateExecutionBuilder;
import com.peergreen.tasks.execution.builder.ParallelExecutionBuilder;
import com.peergreen.tasks.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.execution.builder.UnitOfWorkExecutionBuilder;
import com.peergreen.tasks.execution.builder.WakeUpExecutionBuilder;
import com.peergreen.tasks.model.Task;

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

        root.addExecutionBuilder(newUnitOfWorkExecutionBuilder(executorService));
        root.addExecutionBuilder(newPipelineExecutionBuilder(root));
        root.addExecutionBuilder(newParallelExecutionBuilder(root));
        root.addExecutionBuilder(newWakeUpExecutionBuilder(root));
        root.addExecutionBuilder(newDelegateExecutionBuilder(root));

        return root;
    }

    private static ExecutionBuilder newDelegateExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        return new DelegateExecutionBuilder(executionBuilderManager);
    }

    private static ExecutionBuilder newWakeUpExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        return new WakeUpExecutionBuilder(executionBuilderManager);
    }

    private static ExecutionBuilder newParallelExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        return new ParallelExecutionBuilder(executionBuilderManager);
    }

    private static ExecutionBuilder newPipelineExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        return new PipelineExecutionBuilder(executionBuilderManager);
    }

    private static ExecutionBuilder newUnitOfWorkExecutionBuilder(ExecutorService executorService) {
        return new UnitOfWorkExecutionBuilder(executorService);
    }
}
