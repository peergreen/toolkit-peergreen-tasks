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

package com.peergreen.tasks.context.internal;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.Reference;
import com.peergreen.tasks.model.Scope;
import com.peergreen.tasks.model.Task;

import java.util.HashSet;
import java.util.Set;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 12/11/12
* Time: 15:11
* To change this template use File | Settings | File Templates.
*/
public class InDepthNameSearchReference<T extends Task> implements Reference<T> {

    private Class<T> type;
    private String name;
    private Set<Task> traversed = new HashSet<Task>();

    public InDepthNameSearchReference(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public T resolve(Breadcrumb breadcrumb) {

        // No root, just return
        Task root = breadcrumb.getRoot();
        if (root == null) {
            return null;
        }

        return traverse(breadcrumb);

    }

    private T traverse(Iterable<Task> tasks) {
        for (Task task : tasks) {
            // Do not traverse Tasks twice
            if (!traversed.contains(task)) {

                // See if the Task is the one we're looking for
                if (accept(task)) {
                    return type.cast(task);
                }

                // If not, traverse it's children (if possible)
                if (task instanceof Scope) {
                    T found = traverse((Scope) task);
                    if (found != null) {
                        return found;
                    }
                }

            }
        }

        return null;
    }

    private boolean accept(Task task) {
        traversed.add(task);
        return task.getName().equals(name) && type.isInstance(task);
    }
}
