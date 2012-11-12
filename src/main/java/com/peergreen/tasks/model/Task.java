package com.peergreen.tasks.model;

import java.beans.PropertyChangeListener;

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

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
