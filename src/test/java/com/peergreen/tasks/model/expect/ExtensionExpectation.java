package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.model.context.TaskContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class ExtensionExpectation implements Expectation {

    private Class<?> type;
    private Object value;

    public ExtensionExpectation(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean verify(TaskContext context) {
        Object returned = context.get(type);
        return returned != null && value.equals(returned);
    }
}
