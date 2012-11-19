package com.peergreen.tasks.model.group;

import com.peergreen.tasks.model.Task;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/11/12
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
public class Group implements Iterable<Task> {
    private Set<Task> tasks = new HashSet<Task>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    public boolean contains(Task task) {
        return tasks.contains(task);
    }
}
