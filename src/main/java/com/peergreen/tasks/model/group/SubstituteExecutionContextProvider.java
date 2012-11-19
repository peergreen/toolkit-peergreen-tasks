package com.peergreen.tasks.model.group;

import com.peergreen.tasks.context.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/11/12
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class SubstituteExecutionContextProvider implements ExecutionContextProvider {

    private Map<Group, MutableExecutionContext> contexts = new HashMap<Group, MutableExecutionContext>();

    public void addGroup(Group group, MutableExecutionContext context) {
        contexts.put(group, context);
    }

    @Override
    public ExecutionContext getExecutionContext(Group group, ExecutionContext context) {
        MutableExecutionContext mec = contexts.get(group);
        if (mec != null) {
            mec.setDelegate(context);
        }
        return mec;
    }

}
