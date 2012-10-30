package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.Collections;

import static com.peergreen.tasks.model.requirement.Requirements.completed;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class TaskContainer extends AbstractTask {

    public TaskContainer() {
        this(null);
    }

    public TaskContainer(String name) {
        super(name);
    }

    protected abstract Collection<Task> getInternalTasks();

    public void addTask(Task task) {
        getInternalTasks().add(task);
    }

    public void addTaskAfter(Task reference, Task added) {
        added.getRequirements().add(completed(reference));
        getInternalTasks().add(added);
    }

    public void addTaskBefore(Task reference, Task added) {
        reference.getRequirements().add(completed(added));
        getInternalTasks().add(added);
    }

    public Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(getInternalTasks());
    }

    public boolean isTerminated() {
        for (Task task : getInternalTasks()) {
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
