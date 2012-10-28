package com.peergreen.tasks.model.tracker;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */
public interface Tracker<T> {
    T newSource(Task source);
    void sourceChanged(Task source, State previous, T bag);
}
