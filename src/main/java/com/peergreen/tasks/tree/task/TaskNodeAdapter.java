package com.peergreen.tasks.tree.task;

import com.peergreen.tasks.model.Scope;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.tree.NodeAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class TaskNodeAdapter implements NodeAdapter<Task> {
    @Override
    public Iterable<Task> getChildren(Task object) {
        if (object instanceof Scope) {
            return (Scope) object;
        }
        return null;
    }
}
