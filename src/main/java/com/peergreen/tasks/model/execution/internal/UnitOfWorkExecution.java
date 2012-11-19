package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecution implements Execution {

    private ExecutorService executorService;
    private TaskContext taskContext;
    private UnitOfWork unitOfWork;

    public UnitOfWorkExecution(ExecutorService executorService, TaskContext taskContext, UnitOfWork unitOfWork) {
        this.executorService = executorService;
        this.taskContext = taskContext;
        this.unitOfWork = unitOfWork;
    }

    public void execute() {
        // Execute/Schedule the unit of work
        unitOfWork.setState(State.SCHEDULED);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                unitOfWork.setState(State.RUNNING);
                try {
                    unitOfWork.getJob().execute(taskContext);
                } catch (Throwable t) {
                    unitOfWork.setState(State.FAILED);
                    return;
                }
                unitOfWork.setState(State.COMPLETED);
            }
        });
    }
}
