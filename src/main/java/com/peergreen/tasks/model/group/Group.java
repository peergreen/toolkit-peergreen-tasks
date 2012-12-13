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

import com.peergreen.tasks.model.Task;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/11/12
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
public class Group implements Iterable<Task> {
    private String name;
    private Set<Task> tasks = new HashSet<Task>();


    public Group() {
        this(null);
    }

    public Group(String name) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = UUID.randomUUID().toString();
        }
    }

    public String getName() {
        return name;
    }

    public void addTask(Task task) {
        tasks.add(task);
        if (task instanceof GroupReference) {
            ((GroupReference) task).setGroup(this);
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    public boolean contains(Task task) {
        if (task instanceof GroupReference) {
            Group other = ((GroupReference) task).getGroup();
            return equals(other);
        }
        return tasks.contains(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }

        Group group = (Group) o;

        return name.equals(group.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
