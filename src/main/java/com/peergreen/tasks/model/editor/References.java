package com.peergreen.tasks.model.editor;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.editor.ref.HierarchicalSearchReference;
import com.peergreen.tasks.model.editor.ref.InDepthNameSearchReference;

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
        if (name.startsWith("@")) {
            return new InDepthNameSearchReference<Pipeline>(Pipeline.class, name.substring(1));
        }

        return new HierarchicalSearchReference<Pipeline>(Pipeline.class, Arrays.asList(name.split("/")));
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
        if (name.startsWith("@")) {
            return new InDepthNameSearchReference<Task>(Task.class, name.substring(1));
        }

        return new HierarchicalSearchReference<Task>(Task.class, Arrays.asList(name.split("/")));
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
        if (name.startsWith("@")) {
            return new InDepthNameSearchReference<Parallel>(Parallel.class, name.substring(1));
        }

        return new HierarchicalSearchReference<Parallel>(Parallel.class, Arrays.asList(name.split("/")));
    }

}
