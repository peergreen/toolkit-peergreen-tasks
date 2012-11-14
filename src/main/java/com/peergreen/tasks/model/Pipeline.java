package com.peergreen.tasks.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Pipeline extends AbstractTask implements Scope {
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
        if (isModifiable()) {
            Task previous = null;
            if (!tasks.isEmpty()) {
                previous = tasks.getFirst();
            }
            tasks.addFirst(task);
            propertyChangeSupport().fireIndexedPropertyChange("tasks", 0, previous, task);
        }
    }

    public void add(Task... tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                addLast(task);
            }
        }
    }

    public void addLast(Task task) {
        if (isModifiable()) {
            tasks.addLast(task);
            propertyChangeSupport().fireIndexedPropertyChange("tasks", tasks.size() - 1, null, task);
        }
    }

    public void addTaskAfter(Task after, Task added) {
        if (isModifiable()) {
            int index = tasks.indexOf(after);
            if (index != -1) {
                Task previous = null;
                if ((index + 1) < tasks.size()) {
                    previous = tasks.get(index + 1);
                }
                tasks.add(index + 1, added);
                propertyChangeSupport().fireIndexedPropertyChange("tasks", index + 1, previous, added);
            }
        }
    }

    public void addTaskBefore(Task before, Task added) {
        if (isModifiable()) {
            int index = tasks.indexOf(before);
            if (index != -1) {
                Task previous = null;
                if (index > 0) {
                    previous = tasks.get(index);
                }
                tasks.add(index, added);
                propertyChangeSupport().fireIndexedPropertyChange("tasks", index, previous, added);
            }
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return getTasks().iterator();
    }
}
