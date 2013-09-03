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

package com.peergreen.tasks.model.group;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Collections;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/11/12
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class GroupTaskContextFactoryTestCase {

    @Mock
    private ExecutionContextProvider provider;

    @Mock
    private ExecutionContext result;

    @Mock
    private Task groupedTask;

    @Mock
    private Task aloneTask;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFactoryIsActivatedOnlyForGroupedTasks() throws Exception {
        Group group = new Group();
        group.addTask(groupedTask);

        when(provider.getExecutionContext(group, null))
                .thenReturn(result);
        when(result.getProperty("hello")).thenReturn("world");

        GroupTaskContextFactory factory = new GroupTaskContextFactory(Collections.singleton(group), provider);

        TaskContext context1 = factory.createTaskContext(null, null, groupedTask);
        assertEquals(context1.getProperty("hello"), "world");

        TaskContext context2 = factory.createTaskContext(mock(ExecutionContext.class), null, aloneTask);
        assertNull(context2.getProperty("hello"));


    }
}
