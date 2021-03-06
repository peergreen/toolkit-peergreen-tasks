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

package com.peergreen.tasks.execution.tracker.state;

import java.io.PrintStream;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/10/12
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class StateTaskTracker extends TaskTracker<Object> {

    private PrintStream stream;

    public StateTaskTracker() {
        this(System.out);
    }

    public StateTaskTracker(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public Object newSource(LiveTask source) {
        return new Object();
    }

    @Override
    public void sourceChanged(LiveTask source, State previous, Object bag) {
        stream.printf(
                "%15s - %9S - %s[%s]%n",
                Thread.currentThread().getName(),
                source.getState().name(),
                source.getClass().getSimpleName(),
                source.getModel().getName()
        );
    }
}
