package com.peergreen.tasks.execution.tracker;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 09:01
 * To change this template use File | Settings | File Templates.
 */
public class TrackerManager implements PropertyChangeListener {
    private List<TaskTracker<?>> trackers = new ArrayList<TaskTracker<?>>();

    public void registerTracker(TaskTracker<?> tracker) {
        trackers.add(tracker);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Task source = (Task) event.getSource();
        State oldValue = (State) event.getOldValue();
        State newValue = (State) event.getNewValue();

        for (TaskTracker<?> tracker : trackers) {
            tracker.stateChanged(source, oldValue, newValue);
        }
    }
}
