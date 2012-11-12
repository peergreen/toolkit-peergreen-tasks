package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecution extends TrackedExecution<Pipeline> implements PropertyChangeListener {

    private Iterator<Task> cursor;
    private ExecutionBuilderManager executionBuilderManager;

    public PipelineExecution(TrackerManager trackerManager, ExecutionBuilderManager executionBuilderManager, Pipeline pipeline) {
        super(trackerManager, pipeline);
        this.executionBuilderManager = executionBuilderManager;
        this.cursor = task().getTasks().iterator();
    }

    public void execute() {
        super.execute();
        task().setState(State.RUNNING);

        // Start execution flow
        executeNext();
    }

    private void executeNext() {
        if (cursor.hasNext()) {
            // Schedule the next one on the list
            Task next = cursor.next();
            next.addPropertyChangeListener("state", this);
            executionBuilderManager.newExecution(next).execute();
        } else {
            // Change Pipeline's state
            task().setState(State.COMPLETED);
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
                task().setState(State.FAILED);
                break;
            case COMPLETED:
                // The inner Task has been completed
                executeNext();
        }
    }
}
