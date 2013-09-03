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

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 13/12/12
 * Time: 16:16
 * To change this template use File | Settings | File Templates.
 */
public interface GroupReference {
    Group getGroup();
    void setGroup(Group group);
}
