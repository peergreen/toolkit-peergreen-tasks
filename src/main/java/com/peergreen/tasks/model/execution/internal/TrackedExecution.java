package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.tracker.TrackerManager;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class TrackedExecution<T extends Task> implements Execution {
    private TrackerManager trackerManager;
    private T task;

    public TrackedExecution(TrackerManager trackerManager, T task) {
        this.trackerManager = trackerManager;
        this.task = task;
        this.task.addStateListener(trackerManager);
    }

    protected T task() {
        return task;
    }

    public void execute() {

    }
}
