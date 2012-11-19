package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.context.TaskContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class BreadcrumbExpectation implements Expectation {

    private String expected;

    public BreadcrumbExpectation(String expected) {
        this.expected = expected;
    }

    @Override
    public boolean verify(TaskContext context) {
        return context.getBreadcrumb().toString().contains(expected);
    }
}
