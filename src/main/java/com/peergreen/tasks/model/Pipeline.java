/*
 * Copyright 2012 Peergreen S.A.S.
 *
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

package com.peergreen.tasks.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Pipeline extends AbstractTask implements Container {
    private final LinkedList<Task> tasks = new LinkedList<Task>();

    public Pipeline() {
        this(null);
    }

    public Pipeline(String name) {
        super(name);
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void addFirst(Task task) {
        if (isModifiable()) {
            tasks.addFirst(task);
        }
    }

    @Override
    public void add(Task... tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                addLast(task);
            }
        }
    }

    public void addLast(Task task) {
        if (isModifiable()) {
            tasks.addLast(task);
        }
    }

    public void addTaskAfter(Task after, Task added) {
        if (isModifiable()) {
            int index = tasks.indexOf(after);
            if (index != -1) {
                tasks.add(index + 1, added);
            }
        }
    }

    public void addTaskBefore(Task before, Task added) {
        if (isModifiable()) {
            int index = tasks.indexOf(before);
            if (index != -1) {
                tasks.add(index, added);
            }
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return getTasks().iterator();
    }
}
