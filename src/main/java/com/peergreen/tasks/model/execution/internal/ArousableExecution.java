package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.ArousableTask;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 09:46
 * To change this template use File | Settings | File Templates.
 */
public class ArousableExecution extends TrackedExecution<ArousableTask> implements PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;

    public ArousableExecution(TrackerManager trackerManager, ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, ArousableTask task) {
        super(trackerManager, task);
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        task.addPropertyChangeListener(this);
    }

    @Override
    public void execute() {
        super.execute();

        task().setState(State.SCHEDULED);
    }

    private void reallyExecute() {
        task().setState(State.RUNNING);

        task().getDelegate().addPropertyChangeListener("state", this);
        executionBuilderManager.newExecution(taskContext.getBreadcrumb(), task().getDelegate()).execute();


    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("wakeUp".equals(event.getPropertyName())) {
            reallyExecute();
        } else if ("state".equals(event.getPropertyName())) {
            State newValue = (State) event.getNewValue();

            switch (newValue) {
                case FAILED:
                    task().setState(State.FAILED);
                    break;
                case COMPLETED:
                    task().setState(State.COMPLETED);
                    break;
            }

        }
    }
}
