package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecution extends TrackedExecution<UnitOfWork> {

    private ExecutorService executorService;
    private TaskContext taskContext;

    public UnitOfWorkExecution(TrackerManager trackerManager, ExecutorService executorService, TaskContext taskContext, UnitOfWork unitOfWork) {
        super(trackerManager, unitOfWork);
        this.taskContext = taskContext;
        this.executorService = executorService;
    }

    public void execute() {
        super.execute();
        // Execute/Schedule the unit of work
        task().setState(State.SCHEDULED);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                task().setState(State.RUNNING);
                try {
                    task().getJob().execute(taskContext);
                } catch (Throwable t) {
                    task().setState(State.FAILED);
                    return;
                }
                task().setState(State.COMPLETED);
            }
        });
    }
}
