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

import java.util.UUID;

import com.peergreen.tasks.model.group.Group;
import com.peergreen.tasks.model.group.GroupReference;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTask implements Task, GroupReference {
    protected String name;
    private Group group;

    private final UUID uuid;
    private final int hashCode;
    private boolean readOnly = false;

    public AbstractTask(String name) {
        this.uuid = UUID.randomUUID();
        this.hashCode = uuid.hashCode();
        this.name = (name == null) ? this.uuid.toString() : name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setReadOnly() {
        readOnly = true;
    }

    protected boolean isModifiable() {
        return !readOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTask)) return false;

        AbstractTask that = (AbstractTask) o;

        return uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public void setGroup(Group group) {
        this.group = group;
    }
}
