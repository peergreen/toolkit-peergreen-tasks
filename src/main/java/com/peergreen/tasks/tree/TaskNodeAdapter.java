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

package com.peergreen.tasks.tree;

import com.peergreen.tasks.model.Scope;
import com.peergreen.tasks.model.Task;
import com.peergreen.tree.NodeAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class TaskNodeAdapter implements NodeAdapter<Task> {
    @Override
    public Iterable<Task> getChildren(Task object) {
        if (object instanceof Scope) {
            return (Scope) object;
        }
        return null;
    }
}
