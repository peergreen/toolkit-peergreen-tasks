package com.peergreen.tasks.model.context;

import com.peergreen.tasks.model.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public class Breadcrumb implements Iterable<Task> {
    private List<Task> tasks;

    public Breadcrumb(Task next) {
        this(null, next);
    }

    public Breadcrumb(Breadcrumb parent, Task next) {
        this.tasks = new ArrayList<Task>();
        if (parent != null) {
            this.tasks.addAll(parent.tasks);
        }
        this.tasks.add(next);
    }

    public Task getRoot() {
        return tasks.iterator().next();
    }

    public Task getParent() {
        if (tasks.size() == 1) {
            return null;
        }
        return tasks.get(tasks.size() - 2);
    }

    public Task getCurrent() {
        return tasks.get(tasks.size() - 1);
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Breadcrumb[");
        sb.append("/");
        for (Iterator<Task> i = tasks.iterator(); i.hasNext();) {
            Task task = i.next();
            sb.append(task.getName());
            if (i.hasNext()) {
                sb.append("/");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
