package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.ArousableTask;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
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
public class ArousableExecution implements Execution, PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private ArousableTask arousableTask;

    public ArousableExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, ArousableTask task) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.arousableTask = task;
        task.addPropertyChangeListener(this);
    }

    @Override
    public void execute() {

        arousableTask.setState(State.SCHEDULED);
    }

    private void reallyExecute() {
        arousableTask.setState(State.RUNNING);

        arousableTask.getDelegate().addPropertyChangeListener("state", this);
        executionBuilderManager.newExecution(taskContext.getBreadcrumb(), arousableTask.getDelegate()).execute();


    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("wakeUp".equals(event.getPropertyName())) {
            reallyExecute();
        } else if ("state".equals(event.getPropertyName())) {
            State newValue = (State) event.getNewValue();

            switch (newValue) {
                case FAILED:
                    arousableTask.setState(State.FAILED);
                    break;
                case COMPLETED:
                    arousableTask.setState(State.COMPLETED);
                    break;
            }

        }
    }
}
