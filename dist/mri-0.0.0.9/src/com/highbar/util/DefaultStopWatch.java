package com.highbar.util;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   A simple stopwatch for performing timing measurements
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class DefaultStopWatch implements StopWatch
{
    private long lastStarted;
    private long elapsedTime;
    private boolean isStarted;


    public void stop()
    {
        if ( isStarted )
        {
            isStarted = false;
            elapsedTime += System.nanoTime() - lastStarted;
        }
    }

    public void start()
    {
        if ( !isStarted )
        {
            lastStarted = System.nanoTime();
            isStarted = true;
        }
    }

    public long getElapsedTime()
    {
        long elapsedTime;

        if ( isStarted )
        {
            elapsedTime = this.elapsedTime + System.nanoTime() - lastStarted;
        }
        else
        {
            elapsedTime = this.elapsedTime;
        }
        
        return elapsedTime;
    }
}