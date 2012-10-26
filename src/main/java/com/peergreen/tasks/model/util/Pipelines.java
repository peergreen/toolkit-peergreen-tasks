package com.peergreen.tasks.model.util;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 26/10/12
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class Pipelines {

    public static Pipeline parallelize(Task... tasks) {
        Pipeline parallel = new Pipeline("parallel");
        for (Task task : tasks) {
            parallel.addTask(task, false);
        }
        return parallel;
    }
}
