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

package com.peergreen.tasks.execution.tracker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.model.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 08:53
 * To change this template use File | Settings | File Templates.
 */
public class TaskTracker<T> implements Tracker<T> {

    private final Map<LiveTask, T> tracked = new ConcurrentHashMap<LiveTask, T>();
    private final Tracker<T> tracker;


    public TaskTracker() {
        this.tracker = this;
    }

    public TaskTracker(Tracker<T> tracker) {
        this.tracker = tracker;
    }

    public void stateChanged(LiveTask source, State previous, State current) {

        // First try if this is a new Task
        if (previous == State.WAITING) {
            // Are we interested in that Task
            T o = tracker.newSource(source);
            if (o != null) {
                tracked.put(source, o);
            }
        }

        // If tracker interested in that task
        if (tracked.containsKey(source)) {
            tracker.sourceChanged(source, previous, tracked.get(source));
        }

        // Clean-up our internal map if the new state is COMPLETED or FAILED
        switch (source.getState()) {
            case COMPLETED:
            case FAILED:
                tracked.remove(source);
        }
    }


    @Override
    public T newSource(LiveTask source) {
        return null;
    }
    @Override
    public void sourceChanged(LiveTask source, State previous, T bag) {

    }

}
