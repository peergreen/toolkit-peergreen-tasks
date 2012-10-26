package com.peergreen.tasks.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
import java.util.UUID;

import static com.peergreen.tasks.model.requirement.Requirements.completed;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Pipeline extends AbstractTask {
    private UUID uuid;
    private Deque<Task> tasks = new ArrayDeque<Task>();

    public Pipeline() {
        this("pipeline");
    }

    public Pipeline(String name) {
        super(name);
        this.uuid = UUID.randomUUID();
    }

    public void addTask(Task task) {
        addTask(task, true);
    }

    public void addTask(Task task, boolean link) {

        if (link) {
            // Automatically add the previous element of the queue as dependency
            // That's an ordering guarantee
            Task previous = tasks.peekLast();
            if (previous != null) {
                task.getRequirements().add(completed(previous));
            }
        }

        // Append the task in the queue
        tasks.offer(task);
    }

    public Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(tasks);
    }

    public boolean isTerminated() {
        for (Task task : tasks) {
            switch (task.getState()) {
                case WAITING:
                case RUNNING:
                case SCHEDULED:
                    return false;
            }
        }
        // tasks are COMPLETED or FAILED
        return true;
    }

    public boolean hasFailures() {
        for (Task task : tasks) {
            if (task.getState() == State.FAILED) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pipeline pipeline = (Pipeline) o;

        if (!uuid.equals(pipeline.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
