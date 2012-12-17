/*
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

package com.peergreen.tasks.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 31/10/12
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public class DefaultExecutionContext implements ExecutionContext {
    private Map<String, Object> properties;
    private Map<Class<?>, List<Object>> objects2;

    public DefaultExecutionContext() {
        this.properties = new ConcurrentHashMap<String, Object>();
        this.objects2 = new HashMap<Class<?>, List<Object>>();
    }

    @Override
    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        Object value = getProperty(name);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        Object value = getProperty(name);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type, T defaultValue) {
        Object value = getProperty(name, defaultValue);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public void removeProperty(String name) {
        properties.remove(name);
    }

    @Override
    public <T> T get(Class<T> type) {
        List<Object> candidates = getCandidates(type);

        if (candidates.isEmpty()) {
            return null;
        } else if (candidates.size() == 1) {
            return type.cast(candidates.iterator().next());
        } else {
            // multiple candidates
            // TODO Need sorting to select the closer instance
            return type.cast(candidates.iterator().next());
        }
    }

    @Override
    public void remove(Object instance) {
        Set<Class<?>> types = findTypes(instance);
        for (Class<?> type : types) {
            getCandidates(type).remove(instance);
        }
    }

    @Override
    public void add(Object instance) {
        Set<Class<?>> types = findTypes(instance);
        for (Class<?> type : types) {
            getCandidates(type).add(instance);
        }
    }

    private List<Object> getCandidates(Class<?> type) {
        List<Object> candidates = objects2.get(type);
        if (candidates == null) {
            candidates = new ArrayList<Object>();
            objects2.put(type, candidates);
        }
        return candidates;
    }

    private  Set<Class<?>> findTypes(Object o) {
        Set<Class<?>> types = new HashSet<Class<?>>();

        types.add(o.getClass());
        types.addAll(findTypes(o.getClass()));

        return types;
    }

    private Set<Class<?>> findTypes(Class<?> type) {
        Set<Class<?>> types = new HashSet<Class<?>>();

        // register super class
        Class<?> superClass = type.getSuperclass();
        if (superClass != null) {
            types.add(superClass);
            Set<Class<?>> superTypes = findTypes(superClass);
            types.addAll(superTypes);
        }

        // register each interface
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            types.add(anInterface);
            Set<Class<?>> superInterfaces = findTypes(anInterface);
            types.addAll(superInterfaces);
        }

        return types;
    }
}
