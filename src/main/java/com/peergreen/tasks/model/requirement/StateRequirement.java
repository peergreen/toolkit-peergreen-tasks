package com.peergreen.tasks.model.requirement;

import com.peergreen.tasks.model.Requirement;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public class StateRequirement implements Requirement {

    private Task task;
    private State expected;

    public StateRequirement(Task task, State expected) {
        this.task = task;
        this.expected = expected;
    }

    @Override
    public boolean verify() {
        return task.getState() == expected;
    }
}
