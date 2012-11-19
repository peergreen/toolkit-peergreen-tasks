package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.context.TaskContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 08/11/12
* Time: 14:57
* To change this template use File | Settings | File Templates.
*/
public class SleepExpectation implements Expectation {

    private final long timeout;

    public SleepExpectation(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean verify(TaskContext context) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;
    }
}
