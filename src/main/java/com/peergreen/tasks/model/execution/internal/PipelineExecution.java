package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecution implements Execution, PropertyChangeListener {

    private ListIterator<Task> cursor;
    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private Pipeline pipeline;
    private Lock lock = new ReentrantLock();

    public PipelineExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, Pipeline pipeline) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.pipeline = pipeline;
        this.pipeline.addPropertyChangeListener("tasks", this);
        this.cursor = pipeline.getTasks().listIterator();
    }

    public void execute() {
        pipeline.setState(State.RUNNING);

        // Start execution flow
        executeNext();
    }

    private void executeNext() {
        lock.lock();
        try {
            if (cursor.hasNext()) {
                // Schedule the next one on the list
                Task next = cursor.next();
                next.addPropertyChangeListener("state", this);
                executionBuilderManager.newExecution(taskContext.getBreadcrumb(), next).execute();
            } else {
                // Change Pipeline's state
                pipeline.setState(State.COMPLETED);
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("state".equals(event.getPropertyName())) {
            stateChange(event);
        } else if ("tasks".equals(event.getPropertyName())) {
            tasksChanged();
        }
    }

    private void tasksChanged() {
        // Need to refresh the Iterator and move it's cursor
        lock.lock();
        try {
            if (cursor.hasPrevious()) {
                Task marker = cursor.previous();
                this.cursor = pipeline.getTasks().listIterator();

                // this algorithm only support 'add' semantic
                boolean reached = false;
                while (cursor.hasNext() && !reached) {
                    cursor.next();
                    if (marker.equals(cursor.previous())) {
                        reached = true;
                    }
                }
            } else {
                // Execution was not started at that time
                // Simply creates a new cursor
                this.cursor = pipeline.getTasks().listIterator();
            }
        } finally {
            lock.unlock();
        }
    }

    private void stateChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
                pipeline.setState(State.FAILED);
                break;
            case COMPLETED:
                // The inner Task has been completed
                executeNext();
        }
    }
}
