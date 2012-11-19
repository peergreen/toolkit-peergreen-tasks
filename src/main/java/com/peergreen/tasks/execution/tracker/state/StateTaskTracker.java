package com.peergreen.tasks.execution.tracker.state;

import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.io.PrintStream;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/10/12
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class StateTaskTracker extends TaskTracker<Object> {

    private PrintStream stream;

    public StateTaskTracker() {
        this(System.out);
    }

    public StateTaskTracker(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public Object newSource(Task source) {
        return new Object();
    }

    @Override
    public void sourceChanged(Task source, State previous, Object bag) {
        stream.printf(
                "%15s - %9S - %s[%s]%n",
                Thread.currentThread().getName(),
                source.getState().name(),
                source.getClass().getSimpleName(),
                source.getName()
        );
    }
}
