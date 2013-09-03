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

import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTaskContext implements TaskContext {

    private Breadcrumb breadcrumb;
    private ExecutionContext executionContext;

    public DefaultTaskContext(ExecutionContext executionContext, Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
        this.executionContext = executionContext;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    @Override
    public <T extends Task> T find(Reference<T> reference) {
        return reference.resolve(breadcrumb);
    }

    @Override
    public void setProperty(String name, Object value) {
        executionContext.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return executionContext.getProperty(name);
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        return executionContext.getProperty(name, defaultValue);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        return executionContext.getProperty(name, type);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type, T defaultValue) {
        return executionContext.getProperty(name, type, defaultValue);
    }

    @Override
    public void removeProperty(String name) {
        executionContext.removeProperty(name);
    }

    @Override
    public <T> T get(Class<T> type) {
        return executionContext.get(type);
    }

    @Override
    public void remove(Object instance) {
        executionContext.remove(instance);
    }

    @Override
    public void add(Object instance) {
        executionContext.add(instance);
    }
}
