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

package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 08/11/12
* Time: 14:57
* To change this template use File | Settings | File Templates.
*/
public class StateExpectation implements TaskExpectation {

    private Task task;
    private State state;
    private State latest;

    public StateExpectation(Task task, State state) {
        this.task = task;
        this.state = state;
    }

    @Override
    public void record(LiveTask live) {
        latest = live.getState();
    }

    @Override
    public boolean verify() {
        return state.equals(latest);
    }

    @Override
    public Task getTask() {
        return task;
    }
}
