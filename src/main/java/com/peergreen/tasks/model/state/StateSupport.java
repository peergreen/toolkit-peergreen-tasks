package com.peergreen.tasks.model.state;

import com.peergreen.tasks.model.Task;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class StateSupport {
    private Task source;
    private Collection<StateListener> listeners;

    public StateSupport(Task source) {
        this.listeners = new ArrayList<StateListener>();
        this.source = source;
    }

    public void addStateListener(StateListener listener) {
        listeners.add(listener);
    }

    public void removeStateListener(StateListener listener) {
        listeners.remove(listener);
    }

    public void fireStateChanged(State previous, State current) {
        for (StateListener listener : listeners) {
            listener.stateChanged(source, previous, current);
        }
    }
}
