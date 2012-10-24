package com.peergreen.tasks.model;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public interface StateListener {
    void stateChanged(Task source, State previous, State current);
}
