package com.peergreen.tasks.model;

import com.peergreen.tasks.model.state.State;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
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
    private Deque<Task> tasks = new ArrayDeque<Task>();

    public Pipeline() {
        this(null);
    }

    public Pipeline(String name) {
        super(name);
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
        // tasks are all COMPLETED or FAILED
        return true;
    }

}
