package com.highbar.tools.mri.jmx;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This interface defines the methods used to remotely control the monitor via the JConsole
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public interface MRIMonitorMBean
{
    public void clear();
    public void start();
    public void stop();
    public void report( String fileName );
}
