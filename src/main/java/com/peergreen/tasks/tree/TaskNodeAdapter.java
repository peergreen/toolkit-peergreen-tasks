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

package com.peergreen.tasks.tree;

import com.peergreen.tasks.model.Scope;
import com.peergreen.tasks.model.Task;
import com.peergreen.tree.NodeAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class TaskNodeAdapter implements NodeAdapter<Task> {
    @Override
    public Iterable<Task> getChildren(Task object) {
        if (object instanceof Scope) {
            return (Scope) object;
        }
        return null;
    }
}
