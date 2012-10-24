package com.peergreen.tasks.model;

import java.util.HashSet;
import java.util.Set;

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
    private Set<Task> dependencies = new HashSet<Task>();
    private StateSupport support = new StateSupport(this);

    public AbstractTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Set<Task> getDependencies() {
        return dependencies;
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

    public void addStateListener(StateListener listener) {
        support.addStateListener(listener);
    }

    public void removeStateListener(StateListener listener) {
        support.removeStateListener(listener);
    }

    @Override
    public boolean isReady() {

        // Tasks ready to be executed are WAITING
        if (state != State.WAITING) {
            return false;
        }

        // Tasks ready to be executed have all their dependencies COMPLETED
        for (Task dep : dependencies) {
            if (dep.getState() != State.COMPLETED) {
                return false;
            }
        }

        return true;

    }
}
