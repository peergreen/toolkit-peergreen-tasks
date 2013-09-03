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

package com.peergreen.tasks.model;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public interface Task {

    String getName();

    /**
     * This method is for use of the Task execution engine only.
     * Once executed, the Task is considered read-only and no further structural
     * modifications (such as additional Tasks in a Pipeline) can be applied.
     * This method is called by the execution when it reach the {@literal RUNNING} {@link State}.
     */
    void setReadOnly();
}
