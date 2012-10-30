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

    private Job job;

    public UnitOfWork(Job job) {
        this(job, null);
    }

    public UnitOfWork(Job job, String name) {
        super(name);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
