package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.context.TaskContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class PropertyNotSetExpectation implements Expectation {

    private String propertyName;

    public PropertyNotSetExpectation(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean verify(TaskContext context) {
        return context.getProperty(propertyName) == null;
    }
}
