package com.peergreen.tasks.model.execution.builder;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.internal.UnitOfWorkExecution;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecutionBuilder implements ExecutionBuilder {

    private ExecutorService executorService;
    private TrackerManager trackerManager;

    public UnitOfWorkExecutionBuilder(ExecutorService executorService, TrackerManager trackerManager) {
        this.executorService = executorService;
        this.trackerManager = trackerManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext, Task task) {
        if (task instanceof UnitOfWork) {
            return new UnitOfWorkExecution(trackerManager, executorService, taskContext, (UnitOfWork) task);
        }
        return null;
    }
}
