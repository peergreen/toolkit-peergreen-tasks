package com.peergreen.tasks.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Pipeline extends AbstractTask {
    private LinkedList<Task> tasks = new LinkedList<Task>();

    public Pipeline() {
        this(null);
    }

    public Pipeline(String name) {
        super(name);
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void addFirst(Task task) {
        tasks.addFirst(task);
    }

    public void add(Task... tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                addLast(task);
            }
        }
    }

    public void addLast(Task task) {
        tasks.addLast(task);
    }

    public void addTaskAfter(Task after, Task added) {
        int index = tasks.indexOf(after);
        tasks.add(index + 1, added);
    }

    public void addTaskBefore(Task before, Task added) {
        int index = tasks.indexOf(before);
        tasks.add(index, added);
    }

}
