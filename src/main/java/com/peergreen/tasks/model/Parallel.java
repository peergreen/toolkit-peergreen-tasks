package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Parallel extends AbstractTask implements Scope {
    private Collection<Task> tasks = new HashSet<Task>();

    public Parallel() {
        this(null);
    }

    public Parallel(String name) {
        super(name);
    }

    public Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(tasks);
    }

    public void add(Task task) {
        if (isModifiable()) {
            this.tasks.add(task);
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return getTasks().iterator();
    }
}
