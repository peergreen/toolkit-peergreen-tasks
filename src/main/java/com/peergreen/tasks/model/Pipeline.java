package com.peergreen.tasks.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.peergreen.tasks.model.requirement.Requirements.completed;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Pipeline extends TaskContainer {
    private List<Task> tasks = new ArrayList<Task>();

    public Pipeline() {
        this(null);
    }

    public Pipeline(String name) {
        super(name);
    }

    @Override
    protected Collection<Task> getInternalTasks() {
        return tasks;
    }

    @Override
    public void addTask(Task task) {
        addLast(task);
    }

    public void addFirst(Task task) {
        if (!tasks.isEmpty()) {
            Task first = tasks.get(0);
            if (first != null) {
                first.getRequirements().add(completed(task));
            }
        }
        tasks.add(0, task);
    }

    public void addLast(Task task) {
        if (!tasks.isEmpty()) {
            // Automatically add the previous element of the queue as dependency
            // That's an ordering guarantee
            Task previous = tasks.get(tasks.size() - 1);
            if (previous != null) {
                task.getRequirements().add(completed(previous));
            }
        }

        // Append the task in the queue
        tasks.add(task);
    }

    @Override
    public void addTaskAfter(Task after, Task added) {
        Task next = getTaskAfter(after);
        if (next != null) {
            // re-link
            // after <-- added <-- next
            next.getRequirements().add(completed(added));
            added.getRequirements().add(completed(after));
            tasks.add(tasks.indexOf(next), added);
        } else {
            // no task after 'after'
            // re-link + place at end
            // after <-- added
            added.getRequirements().add(completed(after));
            tasks.add(added);
        }
    }

    @Override
    public void addTaskBefore(Task before, Task added) {
        Task previous = getTaskBefore(before);
        if (previous != null) {
            // re-link
            // previous <-- added <-- before
            before.getRequirements().add(completed(added));
            added.getRequirements().add(completed(previous));
            tasks.add(tasks.indexOf(before), added);
        } else {
            // no task before 'before'
            // re-link + place at beginning
            // added <-- before
            added.getRequirements().add(completed(before));
            tasks.add(0, added);
        }
    }

    private Task getTaskAfter(Task reference) {
        boolean found = false;
        for (Task task : tasks) {
            if (found) {
                return task;
            }
            if (reference.equals(task)) {
                found = true;
            }
        }
        return null;
    }

    private Task getTaskBefore(Task reference) {
        boolean found = false;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            if (found) {
                return task;
            }
            if (reference.equals(task)) {
                found = true;
            }
        }
        return null;
    }

}
