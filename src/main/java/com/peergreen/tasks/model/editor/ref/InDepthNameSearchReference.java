package com.peergreen.tasks.model.editor.ref;

import com.peergreen.tasks.model.ScopingTask;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.editor.Reference;

import java.util.HashSet;
import java.util.Set;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 12/11/12
* Time: 15:11
* To change this template use File | Settings | File Templates.
*/
public class InDepthNameSearchReference<T extends Task> implements Reference<T> {

    private Class<T> type;
    private String name;
    private Set<Task> traversed = new HashSet<Task>();

    public InDepthNameSearchReference(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public T resolve(Breadcrumb breadcrumb) {

        // No root, just return
        Task root = breadcrumb.getRoot();
        if (root == null) {
            return null;
        }

        return traverse(breadcrumb);

    }

    private T traverse(Iterable<Task> tasks) {
        for (Task task : tasks) {
            // Do not traverse Tasks twice
            if (!traversed.contains(task)) {

                // See if the Task is the one we're looking for
                if (accept(task)) {
                    return type.cast(task);
                }

                // If not, traverse it's children (if possible)
                if (task instanceof ScopingTask) {
                    T found = traverse((ScopingTask) task);
                    if (found != null) {
                        return found;
                    }
                }

            }
        }

        return null;
    }

    private boolean accept(Task task) {
        traversed.add(task);
        return task.getName().equals(name) && type.isInstance(task);
    }
}
