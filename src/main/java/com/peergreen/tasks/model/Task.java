package com.peergreen.tasks.model;

import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public interface Task {

    String getName();

    State getState();

    void setState(State state);

    void addStateListener(StateListener listener);

    void removeStateListener(StateListener listener);
}
