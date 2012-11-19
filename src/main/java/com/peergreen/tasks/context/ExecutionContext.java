package com.peergreen.tasks.context;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 17/11/12
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutionContext {

    void setProperty(String name, Object value);
    Object getProperty(String name);
    Object getProperty(String name, Object defaultValue);
    <T> T getProperty(String name, Class<T> type);
    <T> T getProperty(String name, Class<T> type, T defaultValue);
    void removeProperty(String name);

    <T> T get(Class<T> type);
    void remove(Object instance);
    void add(Object instance);

}
