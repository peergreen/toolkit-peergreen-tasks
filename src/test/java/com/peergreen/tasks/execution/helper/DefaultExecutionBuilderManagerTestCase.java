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

package com.peergreen.tasks.execution.helper;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/12/12
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class DefaultExecutionBuilderManagerTestCase {

    @Test
    public void testSimpleBuilderSelection() throws Exception {

        DefaultExecutionBuilderManager manager = new DefaultExecutionBuilderManager();
        manager.addExecutionBuilder(Pipeline.class, new PipelineExecutionBuilder(manager));

        Execution e = manager.newExecution(mock(ExecutionContext.class),
                new Breadcrumb(mock(Task.class)),
                new Pipeline());
        assertNotNull(e);
    }

    @Test
    public void testBuilderSelectionWithInheritance() throws Exception {

        DefaultExecutionBuilderManager manager = new DefaultExecutionBuilderManager();
        manager.addExecutionBuilder(Pipeline.class, new PipelineExecutionBuilder(manager));

        Execution e = manager.newExecution(mock(ExecutionContext.class),
                new Breadcrumb(mock(Task.class)),
                new Phase());
        assertNotNull(e);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testBuilderSelectionWithNoCompatibleBuilder() throws Exception {

        DefaultExecutionBuilderManager manager = new DefaultExecutionBuilderManager();

        Execution e = manager.newExecution(mock(ExecutionContext.class),
                new Breadcrumb(mock(Task.class)),
                new Pipeline());
        assertNotNull(e);
    }

    private class Phase extends Pipeline {}
}
