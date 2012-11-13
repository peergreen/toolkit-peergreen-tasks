package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.model.context.TaskContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class PropertyExpectation implements Expectation {

    private String propertyName;
    private Object value;

    public PropertyExpectation(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public boolean verify(TaskContext context) {
        Object returned = context.getProperties().get(propertyName);
        return returned != null && value.equals(returned);
    }
}