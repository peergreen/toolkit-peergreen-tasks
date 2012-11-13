package com.peergreen.tasks.model.tracker;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 08:53
 * To change this template use File | Settings | File Templates.
 */
public class TaskTracker<T> implements Tracker<T> {

    private Map<Task, T> tracked = new HashMap<Task, T>();
    private Tracker<T> tracker;


    public TaskTracker() {
        this.tracker = this;
    }

    public TaskTracker(Tracker<T> tracker) {
        this.tracker = tracker;
    }

    public void stateChanged(Task source, State previous, State current) {

        // First try if this is a new Task
        if (previous == State.WAITING) {
            // Are we interested in that Task
            T o = tracker.newSource(source);
            if (o != null) {
                tracked.put(source, o);
            }
        }

        // If tracker interested in that task
        if (tracked.containsKey(source)) {
            tracker.sourceChanged(source, previous, tracked.get(source));
        }

        // Clean-up our internal map if the new state is COMPLETED or FAILED
        switch (source.getState()) {
            case COMPLETED:
            case FAILED:
                tracked.remove(source);
        }
    }


    public T newSource(Task source) {
        return null;
    }
    public void sourceChanged(Task source, State previous, T bag) {

    }

}
