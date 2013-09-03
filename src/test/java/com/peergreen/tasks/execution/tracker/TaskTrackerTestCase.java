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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class TaskTrackerTestCase {

    @Mock
    private Task source;
    @Mock
    private LiveTask live;
    @Mock
    private Tracker<String> tracker;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTrackerIsNotifiedWhenANewTaskAppears() throws Exception {
        when(live.getState()).thenReturn(State.RUNNING);

        TaskTracker<String> taskTracker = new TaskTracker<String>(tracker);
        taskTracker.stateChanged(live, State.WAITING, State.RUNNING);

        verify(tracker).newSource(live);
    }

    @Test
    public void testUninterestedTrackerIsNotNotified() throws Exception {
        when(live.getState()).thenReturn(State.RUNNING);

        TaskTracker<String> taskTracker = new TaskTracker<String>(tracker);
        taskTracker.stateChanged(live, State.WAITING, State.RUNNING);

        verify(tracker).newSource(live);
        verify(tracker, times(0)).sourceChanged(live, State.WAITING, null);
    }

    @Test
    public void testInterestedTrackerIsNotified() throws Exception {
        when(live.getState()).thenReturn(State.RUNNING);
        when(tracker.newSource(live)).thenReturn("ok");

        TaskTracker<String> taskTracker = new TaskTracker<String>(tracker);
        taskTracker.stateChanged(live, State.WAITING, State.RUNNING);

        verify(tracker).sourceChanged(live, State.WAITING, "ok");
    }
}
