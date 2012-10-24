package com.peergreen.tasks.model;

import com.peergreen.tasks.runtime.Job;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class Task {

    private String name;
    private UUID uuid;
    private Job job;
    private State state = State.WAITING;

    private Set<Task> dependencies = new HashSet<Task>();

    public Task(Job job) {
        this(job, "unnamed");
    }

    public Task(Job job, String name) {
        this.job = job;
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public Set<Task> getDependencies() {
        return dependencies;
    }

    public Job getJob() {
        return job;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (uuid != null ? !uuid.equals(task.uuid) : task.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
