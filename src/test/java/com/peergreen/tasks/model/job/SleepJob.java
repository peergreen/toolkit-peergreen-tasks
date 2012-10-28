package com.peergreen.tasks.model.job;

import com.peergreen.tasks.runtime.Job;
import com.peergreen.tasks.runtime.JobContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 28/10/12
* Time: 08:24
* To change this template use File | Settings | File Templates.
*/
public class SleepJob implements Job {
    private long time;

    public SleepJob(long time) {
        this.time = time;
    }

    @Override
    public void execute(JobContext context) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // Ignored
        }
    }
}
