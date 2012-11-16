package com.peergreen.tasks.model.job;

import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.expect.Expectation;
import com.peergreen.tasks.runtime.Job;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 08/11/12
* Time: 14:58
* To change this template use File | Settings | File Templates.
*/
public class ExpectationsJob implements Job {
    public boolean passed;
    private Collection<Expectation> expectations;

    public ExpectationsJob(Expectation expectation) {
        this(Collections.singleton(expectation));
    }

    public ExpectationsJob(Expectation... expectations) {
        this(Arrays.asList(expectations));
    }

    public ExpectationsJob(Collection<Expectation> expectations) {
        this.expectations = expectations;
    }

    @Override
    public void execute(TaskContext context) throws Exception {
        passed = true;
        for (Expectation expectation : expectations) {
            if (!expectation.verify(context)) {
                passed = false;
                return;
            }
        }
    }
}
