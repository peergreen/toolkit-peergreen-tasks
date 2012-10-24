package com.peergreen.tasks.runtime;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class JobException extends Exception {
    public JobException(String s) {
        super(s);
    }

    public JobException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
