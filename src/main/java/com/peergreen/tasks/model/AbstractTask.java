/*
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

package com.peergreen.tasks.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTask implements Task {
    protected String name;
    private State state = State.WAITING;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    private UUID uuid;

    public AbstractTask(String name) {
        this.uuid = UUID.randomUUID();
        this.name = (name == null) ? this.uuid.toString() : name;
    }

    public String getName() {
        return name;
    }

    @Override
    public State getState() {
        return state;
    }

    protected PropertyChangeSupport propertyChangeSupport() {
        return support;
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

    protected boolean isModifiable() {
        switch (state) {
            case WAITING:
            case SCHEDULED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTask)) return false;

        AbstractTask that = (AbstractTask) o;

        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
