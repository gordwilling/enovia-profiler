package com.highbar.util;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   An interface for implementing a simple stop watch which can be used to record
 *   the execution times of various operations
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public interface StopWatch
{
    /**
     * Null implementation (each method is a NOOP)
     */
     public static final StopWatch NULL = new StopWatch() {
        public void stop(){}
        public void start(){}
        public long getElapsedTime() { return 0; }
    };

    /**
     * Starts the timer
     */
    void start();

    /**
     * Stops the timer
     */
    void stop();

    /**
     * @return the total time recorded
     */
    long getElapsedTime();
}
