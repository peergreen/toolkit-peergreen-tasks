package com.peergreen.tasks.model;

import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;
import com.peergreen.tasks.model.state.StateSupport;

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
    private StateSupport support = new StateSupport(this);

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

    @Override
    public void setState(State state) {
        State previous = this.state;
        this.state = state;
        support.fireStateChanged(previous, state);
    }

    @Override
    public void addStateListener(StateListener listener) {
        support.addStateListener(listener);
    }

    @Override
    public void removeStateListener(StateListener listener) {
        support.removeStateListener(listener);
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
