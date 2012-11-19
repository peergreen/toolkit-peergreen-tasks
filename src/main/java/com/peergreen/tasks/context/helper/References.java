package com.peergreen.tasks.context.helper;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.Reference;
import com.peergreen.tasks.context.internal.HierarchicalSearchReference;
import com.peergreen.tasks.context.internal.InDepthNameSearchReference;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 30/10/12
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class References {
    public static Reference<Pipeline> pipeline(final Pipeline pipeline) {
        return new Reference<Pipeline>() {
            @Override
            public Pipeline resolve(Breadcrumb breadcrumb) {
                return pipeline;
            }
        };
    }
    public static Reference<Pipeline> pipeline(final String name) {
        return search(name, Pipeline.class);
    }

    public static Reference<Task> task(final Task task) {
        return new Reference<Task>() {
            @Override
            public Task resolve(Breadcrumb breadcrumb) {
                return task;
            }
        };
    }
    public static Reference<Task> task(final String name) {
        return search(name, Task.class);
    }

    public static Reference<Parallel> parallel(final Parallel parallel) {
        return new Reference<Parallel>() {
            @Override
            public Parallel resolve(Breadcrumb breadcrumb) {
                return parallel;
            }
        };
    }
    public static Reference<Parallel> parallel(final String name) {
        return search(name, Parallel.class);
    }

    private static <T extends Task> Reference<T> search(String name, Class<T> type) {
        if (name.startsWith("@")) {
            return new InDepthNameSearchReference<T>(type, name.substring(1));
        }

        return new HierarchicalSearchReference<T>(type, Arrays.asList(name.split("/")));
    }

}
