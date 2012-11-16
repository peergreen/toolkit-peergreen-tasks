package com.peergreen.tasks.model.job;

import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.runtime.Job;

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
    public void execute(TaskContext context) throws Exception {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // Ignored
        }
    }
}
