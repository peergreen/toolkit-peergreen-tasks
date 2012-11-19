package com.peergreen.tasks.model.group;

import com.peergreen.tasks.context.ExecutionContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/11/12
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutionContextProvider {
    ExecutionContext getExecutionContext(Group group, ExecutionContext context);
}
