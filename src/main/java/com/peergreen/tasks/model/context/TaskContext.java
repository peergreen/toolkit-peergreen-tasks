package com.peergreen.tasks.model.context;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.editor.Reference;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public interface TaskContext extends ExecutionContext {
    Breadcrumb getBreadcrumb();
    <T extends Task> T find(Reference<T> reference);
}
