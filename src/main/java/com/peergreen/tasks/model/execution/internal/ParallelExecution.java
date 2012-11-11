package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecution extends TrackedExecution<Parallel> implements StateListener {

    private ExecutionBuilderManager executionBuilderManager;
    private AtomicInteger completed = new AtomicInteger(0);
    private State out = State.COMPLETED;

    public ParallelExecution(TrackerManager trackerManager, ExecutionBuilderManager executionBuilderManager, Parallel parallel) {
        super(trackerManager, parallel);
        this.executionBuilderManager = executionBuilderManager;
    }

    public void execute() {
        super.execute();
        task().setState(State.RUNNING);

        // Start execution flow
        executeAll();
    }

    private void executeAll() {
        for (Task task : task().getTasks()) {
            task.addStateListener(this);
            executionBuilderManager.newExecution(task).execute();
        }
    }


    @Override
    public void stateChanged(Task source, State previous, State current) {

        switch (current) {
            case FAILED:
                out = State.FAILED;
            case COMPLETED:
                // The inner Task has been completed
                if (completed.incrementAndGet() == task().getTasks().size()) {
                    // All tasks have been executed
                    // The parallel is now either FAILED or COMPLETED
                    task().setState(out);
                }
        }

    }

}
