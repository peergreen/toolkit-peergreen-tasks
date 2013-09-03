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

package com.peergreen.tasks.model.job;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Job;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 28/10/12
* Time: 08:24
* To change this template use File | Settings | File Templates.
*/
public class SleepJob implements Job {
    private long time;

    public SleepJob(long time) {
        this.time = time;
    }

    @Override
    public void execute(TaskContext context) throws Exception {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // Ignored
        }
    }
}
