package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractExecution implements StateListener {

    private ExecutorService executorService;
    private TrackerManager trackerManager;

    public AbstractExecution(ExecutorService executorService) {
        this(executorService, new TrackerManager());
    }

    protected AbstractExecution(AbstractExecution parent) {
        this(parent.executorService, parent.trackerManager);
    }

    public AbstractExecution(ExecutorService executorService, TrackerManager trackerManager) {
        this.executorService = executorService;
        this.trackerManager = trackerManager;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    protected void executeTask(Task task) {
        if (task instanceof UnitOfWork) {
            executeUnitOfWork((UnitOfWork) task);
        } else if (task instanceof Pipeline) {
            executePipeline((Pipeline) task);
        } else if (task instanceof Parallel) {
            executeParallel((Parallel) task);
        } // Unknown type, error ?
    }

    private void executeParallel(Parallel parallel) {

        parallel.addStateListener(this);

        ParallelExecution inner = new ParallelExecution(this, parallel);
        inner.execute();
    }

    protected void executePipeline(Pipeline sub) {

        sub.addStateListener(this);

        PipelineExecution inner = new PipelineExecution(this, sub);
        inner.execute();
    }

    protected void executeUnitOfWork(final UnitOfWork unitOfWork) {
        // Execute/Schedule the unit of work
        unitOfWork.addStateListener(this);
        UnitOfWorkExecution unitOfWorkExecution = new UnitOfWorkExecution(this, unitOfWork);
        unitOfWorkExecution.execute();
    }

    @Override
    public void stateChanged(Task source, State previous, State current) {
        // Empty
    }

    public abstract void execute();
}
