package com.peergreen.tasks.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Pipeline implements TaskListener {
    private String name;
    private UUID uuid;
    private Deque<Task> tasks = new ArrayDeque<Task>();

    public Pipeline() {
        this("pipeline");
    }

    public Pipeline(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public void addTask(Task task) {

        // Automatically add the previous element of the queue as dependency
        // That's an ordering guarantee
        Task previous = tasks.peekLast();
        if (previous != null) {
            task.getDependencies().add(previous);
        }
        tasks.offer(task);
    }

    public Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(tasks);
    }

    @Override
    public void taskCompleted(Task completed) {
        for (Task task : tasks) {
            task.getDependencies().remove(completed);
        }
    }
}
