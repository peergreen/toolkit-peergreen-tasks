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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.model.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 09:01
 * To change this template use File | Settings | File Templates.
 */
public class TrackerManager implements PropertyChangeListener {
    private List<TaskTracker<?>> trackers = new ArrayList<TaskTracker<?>>();

    public void registerTracker(TaskTracker<?> tracker) {
        trackers.add(tracker);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        LiveTask source = (LiveTask) event.getSource();
        State oldValue = (State) event.getOldValue();
        State newValue = (State) event.getNewValue();

        for (TaskTracker<?> tracker : trackers) {
            tracker.stateChanged(source, oldValue, newValue);
        }
    }
}
