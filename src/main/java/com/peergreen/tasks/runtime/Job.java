package com.peergreen.tasks.runtime;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public interface Job {
    void execute(JobContext context);
}
