package com.peergreen.tasks.context;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.Breadcrumb;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 30/10/12
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
public interface Reference<T extends Task> {
    T resolve(Breadcrumb breadcrumb);
}
