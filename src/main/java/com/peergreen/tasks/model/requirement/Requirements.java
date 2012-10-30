package com.peergreen.tasks.model.requirement;

import com.peergreen.tasks.model.Requirement;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class Requirements {

    public static Requirement waiting(Task task) {
        return new StateRequirement(task, State.WAITING);
    }

    public static Requirement completed(Task task) {
        return new StateRequirement(task, State.COMPLETED);
    }

    public static Requirement failed(Task task) {
        return new StateRequirement(task, State.FAILED);
    }
}
