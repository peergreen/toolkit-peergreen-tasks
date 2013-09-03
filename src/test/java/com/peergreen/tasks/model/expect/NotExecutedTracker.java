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
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/12/12
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class NotExecutedTracker extends TaskTracker<Object> {
    private Task absent;
    private boolean found = false;

    public NotExecutedTracker(Task absent) {
        this.absent = absent;
    }

    @Override
    public Object newSource(LiveTask source) {
        if (absent.equals(source.getModel())) {
            found = true;
        }
        return null;
    }

    public boolean verify() {
        return !found;
    }

}
