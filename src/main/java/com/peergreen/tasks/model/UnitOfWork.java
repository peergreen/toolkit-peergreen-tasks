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
public class UnitOfWork extends AbstractTask {

    private UUID uuid;
    private Job job;

    public UnitOfWork(Job job) {
        this(job, "unnamed");
    }

    public UnitOfWork(Job job, String name) {
        super(name);
        this.job = job;
        this.uuid = UUID.randomUUID();
    }

    public Job getJob() {
        return job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnitOfWork that = (UnitOfWork) o;

        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
