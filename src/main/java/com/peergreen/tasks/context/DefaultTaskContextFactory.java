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

package com.peergreen.tasks.context;

import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.model.Task;

public class DefaultTaskContextFactory implements TaskContextFactory {
    @Override
    public TaskContext createTaskContext(ExecutionContext parent, Breadcrumb breadcrumb, Task task) {
        return new DefaultTaskContext(parent, new Breadcrumb(breadcrumb, task));
    }
}