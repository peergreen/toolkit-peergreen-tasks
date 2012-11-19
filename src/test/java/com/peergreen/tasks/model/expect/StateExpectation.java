package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.TaskContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 08/11/12
* Time: 14:57
* To change this template use File | Settings | File Templates.
*/
public class StateExpectation implements Expectation {

    private Task task;
    private State state;

    public StateExpectation(Task task, State state) {
        this.task = task;
        this.state = state;
    }

    @Override
    public boolean verify(TaskContext context) {
        return task.getState() == state;
    }
}
