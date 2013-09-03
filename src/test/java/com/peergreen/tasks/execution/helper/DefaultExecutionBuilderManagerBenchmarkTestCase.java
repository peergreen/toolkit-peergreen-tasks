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

import org.testng.annotations.Test;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.DefaultExecutionContext;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/12/12
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public class DefaultExecutionBuilderManagerBenchmarkTestCase extends SimpleBenchmark {

    @Param({"10", "100", "1000"})
    private int length;
    @Param
    private BuilderManagerSupport managerSupport;
    @Param
    private TaskProvider provider;

    private DefaultExecutionBuilderManager manager;
    private ExecutionContext executionContext;
    private Breadcrumb breadcrumb;
    private Task task;

    @Test(groups = "caliper")
    public static void benchmark() throws Exception {
        Runner.main(DefaultExecutionBuilderManagerBenchmarkTestCase.class, new String[]{});
    }

    @Override
    protected void setUp() throws Exception {

        manager = new DefaultExecutionBuilderManager();
        managerSupport.setUpManager(manager);
        task = provider.create();

        executionContext = new DefaultExecutionContext();
        breadcrumb = new Breadcrumb(task);

    }

    public void timeNewExecution(int reps) {
        for (int i = 0; i < reps; i++) {
            manager.newExecution(executionContext, breadcrumb, task);
        }
    }

    public enum BuilderManagerSupport {
        PIPELINE_ONLY {
            @Override
            void setUpManager(DefaultExecutionBuilderManager manager) {
                manager.addExecutionBuilder(Pipeline.class, new PipelineExecutionBuilder(manager));
            }
        },
        PIPELINE_AND_PHASE {
            @Override
            void setUpManager(DefaultExecutionBuilderManager manager) {
                manager.addExecutionBuilder(Pipeline.class, new PipelineExecutionBuilder(manager));
                manager.addExecutionBuilder(Phase.class, new PipelineExecutionBuilder(manager));

            }
        };

        abstract void setUpManager(DefaultExecutionBuilderManager manager);
    }

    public enum TaskProvider {
        PIPELINE {
            Task create() {
                return new Pipeline();
            }
        },
        PHASE {
            Task create() {
                return new Phase();
            }
        };

        abstract Task create();
    }

    private static class Phase extends Pipeline {}
}
