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
public class EmptyJob implements Job {
    @Override
    public void execute(JobContext context) {
        // do nothing
    }
}
