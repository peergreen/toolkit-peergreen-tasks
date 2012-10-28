package com.peergreen.tasks.model.tracker.time;

import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/10/12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public interface TimesVisitor {
    void visitDuration(Task task, long duration);
}
