/*
 * Copyright 2012 Peergreen S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.execution.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.model.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/12/12
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractExecution implements Execution {

    private State state = State.WAITING;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        State previous = this.state;
        this.state = state;
        support.firePropertyChange("state", previous, state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }
}
