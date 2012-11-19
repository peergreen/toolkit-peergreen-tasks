package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.WakeUp;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 09:46
 * To change this template use File | Settings | File Templates.
 */
public class WakeUpExecution implements Execution, PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private WakeUp wakeUp;

    public WakeUpExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, WakeUp task) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.wakeUp = task;
        task.addPropertyChangeListener(this);
    }

    @Override
    public void execute() {

        wakeUp.setState(State.SCHEDULED);
    }

    private void reallyExecute() {
        wakeUp.setState(State.RUNNING);

        wakeUp.getDelegate().addPropertyChangeListener("state", this);
        executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), wakeUp.getDelegate()).execute();


    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("wakeUp".equals(event.getPropertyName())) {
            reallyExecute();
        } else if ("state".equals(event.getPropertyName())) {
            State newValue = (State) event.getNewValue();

            switch (newValue) {
                case FAILED:
                    wakeUp.setState(State.FAILED);
                    break;
                case COMPLETED:
                    wakeUp.setState(State.COMPLETED);
                    break;
            }

        }
    }
}
