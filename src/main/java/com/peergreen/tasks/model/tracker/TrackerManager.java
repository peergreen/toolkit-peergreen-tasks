package com.peergreen.tasks.model.tracker;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 09:01
 * To change this template use File | Settings | File Templates.
 */
public class TrackerManager implements StateListener {
    private List<TaskTracker<?>> trackers = new ArrayList<TaskTracker<?>>();

    public void registerTracker(TaskTracker<?> tracker) {
        trackers.add(tracker);
    }

    @Override
    public void stateChanged(Task source, State previous, State current) {
        for (TaskTracker<?> tracker : trackers) {
            tracker.stateChanged(source, previous, current);
        }
    }
}
