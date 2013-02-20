/*
 * Copyright 2012 Peergreen S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.execution.LiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class BreadcrumbExpectation implements Expectation {

    private String expected;
    private String latest;

    public BreadcrumbExpectation(String expected) {
        this.expected = expected;
    }

    @Override
    public void record(LiveTask live) {
        latest = live.getContext().getBreadcrumb().toString();
    }

    @Override
    public boolean verify() {
        return latest.contains(expected);
    }
}
