package com.peergreen.tasks.model;

import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.peergreen.tasks.model.util.Pipelines.parallelize;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Execution implements StateListener {

    private ExecutorService executorService;
    private TrackerManager trackerManager = new TrackerManager();
    private TaskContainer container;

    private Map<TaskContainer, Execution> executions = new HashMap<TaskContainer, Execution>();

    public Execution(ExecutorService executorService, TaskContainer... containers) {
        this(executorService, parallelize(containers));
    }

    public Execution(ExecutorService executorService, TaskContainer container) {
        this.executorService = executorService;
        this.container = container;
    }

    protected Execution(Execution parent, TaskContainer container) {
        this.executorService = parent.executorService;
        this.trackerManager = parent.trackerManager;
        this.container = container;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    public void start() {
        container.addStateListener(trackerManager);
        container.setState(State.RUNNING);

        // Start execution flow
        executeContainer();
    }

    private void executeContainer() {
        // Collect the runnable tasks of the container
        Collection<Task> runnable = filter(container.getTasks());

        for (Task task : runnable) {
            executeTask(task);
        }

        for (Execution execution : executions.values()) {
            execution.executeContainer();
        }

    }

    private void executeTask(Task task) {
        if (task instanceof UnitOfWork) {
            executeUnitOfWork((UnitOfWork) task);
        } else if (task instanceof TaskContainer) {
            executeSubContainer((TaskContainer) task);
        } // Unknown type, error ?
    }

    private void executeSubContainer(TaskContainer sub) {

        Execution inner = executions.get(sub);
        if (inner == null) {
            sub.addStateListener(trackerManager);
            sub.addStateListener(this);
            inner = new Execution(this, sub);
            executions.put(sub, inner);

            inner.start();
        } else {
            inner.executeContainer();
        }
    }

    private void executeUnitOfWork(final UnitOfWork unitOfWork) {
        // Execute/Schedule the unit of work
        unitOfWork.addStateListener(trackerManager);
        unitOfWork.addStateListener(this);
        unitOfWork.setState(State.SCHEDULED);
        executorService.submit(new Runnable() {
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

    private Collection<Task> filter(Collection<Task> tasks) {
        Collection<Task> filtered = new HashSet<Task>();
        for (Task task : tasks) {
            if (task.isReady()) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    @Override
    public void stateChanged(Task source, State previous, State current) {

        // Try to find some new tasks to execute
        executeContainer();

        // If the task has failed, change the global state of the executed container
        if ((current == State.FAILED) && (container.getState() != State.FAILED)) {
            container.setState(State.FAILED);
        }

        // If the task has completed without problems AND that there are no more task to execute
        if ((current == State.COMPLETED)
                && (container.getState() != State.COMPLETED)
                && (container.isTerminated())) {
            container.setState(State.COMPLETED);
        }

    }
}
