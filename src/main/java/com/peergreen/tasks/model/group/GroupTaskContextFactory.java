/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.model.group;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.model.Task;

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
