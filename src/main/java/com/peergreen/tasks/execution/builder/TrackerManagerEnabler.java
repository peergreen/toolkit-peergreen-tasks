package com.peergreen.tasks.execution.builder;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.tracker.TrackerManager;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 13/11/12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public class TrackerManagerEnabler implements ExecutionBuilder {

    private TrackerManager trackerManager;

    public TrackerManagerEnabler() {
        this(new TrackerManager());
    }

    public TrackerManagerEnabler(TrackerManager trackerManager) {
        this.trackerManager = trackerManager;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext) {
        Task task = taskContext.getBreadcrumb().getCurrent();
        task.addPropertyChangeListener("state", trackerManager);

        // Do not return anything to not break the builder chain
        return null;
    }
}
