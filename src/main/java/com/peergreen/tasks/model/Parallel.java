/**
 * Copyright 2012-2013 Peergreen S.A.S. All rights reserved.
 * Proprietary and confidential.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class Parallel extends AbstractTask implements Container {
    private final Collection<Task> tasks = Collections.synchronizedSet(new HashSet<Task>());

    public Parallel() {
        this(null);
    }

    public Parallel(String name) {
        super(name);
    }

    public Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(tasks);
    }

    @Override
    public void add(Task... tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                if (isModifiable()) {
                    this.tasks.add(task);
                }
            }
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return getTasks().iterator();
    }
}
