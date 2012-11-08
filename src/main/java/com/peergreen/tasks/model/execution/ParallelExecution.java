package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecution extends AbstractExecution {

    private Parallel parallel;
    private AtomicInteger completed = new AtomicInteger(0);
    private State out = State.COMPLETED;

    public ParallelExecution(ExecutorService executorService, Parallel parallel) {
        super(executorService);
        this.parallel = parallel;
    }

    protected ParallelExecution(AbstractExecution parent, Parallel parallel) {
        super(parent);
        this.parallel = parallel;

        parallel.addStateListener(parent);
    }

    public void execute() {
        parallel.addStateListener(getTrackerManager());
        parallel.setState(State.RUNNING);

        // Start execution flow
        executeAll();
    }

    private void executeAll() {
        for (Task task : parallel.getTasks()) {
            executeTask(task);
        }
    }


    @Override
    public void stateChanged(Task source, State previous, State current) {

        switch (current) {
            case FAILED:
                out = State.FAILED;
            case COMPLETED:
                // The inner Task has been completed
                if (completed.incrementAndGet() == parallel.getTasks().size()) {
                    // All tasks have been executed
                    // The parallel is now either FAILED or COMPLETED
                    parallel.setState(out);
                }
        }

    }

}
