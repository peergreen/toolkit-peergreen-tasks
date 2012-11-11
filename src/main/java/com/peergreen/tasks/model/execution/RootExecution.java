package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class RootExecution implements Execution, ExecutionBuilderManager {

    private List<ExecutionBuilder> builders = new ArrayList<ExecutionBuilder>();
    private TrackerManager trackerManager = new TrackerManager();
    private Task task;

    public RootExecution(Task task) {
        this.task = task;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    public void addExecutionBuilder(final ExecutionBuilder builder) {
        builders.add(builder);
    }

    @Override
    public Execution newExecution(final Task task) {
        for (ExecutionBuilder builder : builders) {
            Execution execution = builder.newExecution(task);
            if (execution != null) {
                return execution;
            }
        }

        throw new IllegalStateException("Cannot find any ExecutionBuilder supporting " + task.getClass());
    }

    public void execute() {
        newExecution(task).execute();
    }


}
