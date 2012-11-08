package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.state.State;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecution extends AbstractExecution {

    private UnitOfWork unitOfWork;

    public UnitOfWorkExecution(ExecutorService executorService, UnitOfWork unitOfWork) {
        super(executorService);
        this.unitOfWork = unitOfWork;
    }

    public UnitOfWorkExecution(AbstractExecution parent, UnitOfWork unitOfWork) {
        super(parent);
        this.unitOfWork = unitOfWork;
    }

    public void execute() {
        // Execute/Schedule the unit of work
        unitOfWork.setState(State.SCHEDULED);
        getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                unitOfWork.setState(State.RUNNING);
                try {
                    unitOfWork.getJob().execute(null);
                } catch (Throwable t) {
                    unitOfWork.setState(State.FAILED);
                    return;
                }
                unitOfWork.setState(State.COMPLETED);
            }
        });
    }
}
