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

package com.peergreen.tasks.execution.builder;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.execution.internal.ParallelExecution;
import com.peergreen.tasks.model.Parallel;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecutionBuilder implements ExecutionBuilder<Parallel> {

    private ExecutionBuilderManager executionBuilderManager;

    public ParallelExecutionBuilder(ExecutionBuilderManager executionBuilderManager) {
        this.executionBuilderManager = executionBuilderManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext, Parallel task) {
        return new ParallelExecution(executionBuilderManager, taskContext, task);
    }
}
