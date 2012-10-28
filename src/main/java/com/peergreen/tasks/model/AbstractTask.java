package com.peergreen.tasks.model;

import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;
import com.peergreen.tasks.model.state.StateSupport;

import java.util.HashSet;
import java.util.Set;

import static com.peergreen.tasks.model.requirement.Requirements.waiting;

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

    private Set<Requirement> requirements = new HashSet<Requirement>();

    public AbstractTask(String name) {
        this.name = name;
        this.requirements.add(waiting(this));
    }

    public String getName() {
        return name;
    }

    @Override
    public Set<Requirement> getRequirements() {
        return requirements;
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

        for (Requirement requirement : requirements) {
            if (!requirement.verify()) {
                return false;
            }
        }
        return true;

    }
}
