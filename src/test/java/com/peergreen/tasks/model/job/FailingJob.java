package com.peergreen.tasks.model.job;

import com.peergreen.tasks.runtime.Job;
import com.peergreen.tasks.runtime.JobContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 08/11/12
* Time: 18:06
* To change this template use File | Settings | File Templates.
*/
public class FailingJob implements Job {
    @Override
    public void execute(JobContext context) {
        throw new RuntimeException("Boom");
    }
}
