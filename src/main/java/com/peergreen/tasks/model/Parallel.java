package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Parallel extends TaskContainer {
    private Collection<Task> tasks = new HashSet<Task>();

    public Parallel() {
        this(null);
    }

    public Parallel(String name) {
        super(name);
    }

    @Override
    protected Collection<Task> getInternalTasks() {
        return tasks;
    }

}
