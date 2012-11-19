package com.peergreen.tasks.execution.helper;

import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.execution.builder.DelegateExecutionBuilder;
import com.peergreen.tasks.execution.builder.ParallelExecutionBuilder;
import com.peergreen.tasks.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.execution.builder.UnitOfWorkExecutionBuilder;
import com.peergreen.tasks.execution.builder.WakeUpExecutionBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/11/12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public class ExecutorServiceBuilderManager extends DefaultExecutionBuilderManager {

    public static final int DEFAULT_POOL_SIZE = 10;

    public ExecutorServiceBuilderManager() {
        this(DEFAULT_POOL_SIZE);
    }

    public ExecutorServiceBuilderManager(int size) {
        this(Executors.newFixedThreadPool(size));
    }

    public ExecutorServiceBuilderManager(ExecutorService executorService) {
        this(new DefaultTaskContextFactory(), executorService);
    }

    public ExecutorServiceBuilderManager(TaskContextFactory taskContextFactory, ExecutorService executorService) {
        super(taskContextFactory);
        addExecutionBuilder(new UnitOfWorkExecutionBuilder(executorService));
        addExecutionBuilder(new PipelineExecutionBuilder(this));
        addExecutionBuilder(new ParallelExecutionBuilder(this));
        addExecutionBuilder(new WakeUpExecutionBuilder(this));
        addExecutionBuilder(new DelegateExecutionBuilder(this));
    }
}
