package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.model.context.TaskContext;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 08/11/12
* Time: 14:57
* To change this template use File | Settings | File Templates.
*/
public interface Expectation {
    boolean verify(TaskContext context);
}