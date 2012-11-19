package com.peergreen.tasks.model.group;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.execution.TaskContextFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/11/12
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */
public class GroupTaskContextFactory implements TaskContextFactory {

    private Set<Group> groups = new HashSet<Group>();
    private ExecutionContextProvider provider;
    private TaskContextFactory delegate = new DefaultTaskContextFactory();

    public GroupTaskContextFactory(Set<Group> groups, ExecutionContextProvider provider) {
        this.groups = groups;
        this.provider = provider;
    }

    @Override
    public TaskContext createTaskContext(ExecutionContext parent, Breadcrumb breadcrumb, Task task) {
        ExecutionContext context = parent;
        for (Group group : groups) {
            if (group.contains(task)) {
                // Overload the ExecutionContext
                // If Task is contained in multiple Groups, chain the last produced ExecutionContext
                ExecutionContext current = context;
                context = provider.getExecutionContext(group, context);

                // Re-use last ExecutionContext if provider returned null
                if (context == null) {
                    context = current;
                }
            }
        }
        return delegate.createTaskContext(context, breadcrumb, task);
    }
}
