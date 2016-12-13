package com.highbar.tools.mri.jmx;

import com.highbar.tools.mri.monitor.MRIProfiler;
import com.highbar.tools.mri.report.MRIReportWriter;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.io.FileNotFoundException;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class enables remote control of the monitor via the JConsole.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MRIMonitor extends NotificationBroadcasterSupport implements MRIMonitorMBean
{
    private long notifications = 0;

    public void clear()
    {
        MRIProfiler.INSTANCE.clear();
        Notification n = new Notification( "Status", this, ++notifications, "Enovia Profiler Log Cleared" );
        sendNotification( n );
    }

    public void start()
    {
        MRIProfiler.INSTANCE.setActive( true );
        Notification n = new Notification( "Status", this, ++notifications, "Enovia Profiler Started" );
        sendNotification( n );
    }

    public void stop()
    {
        MRIProfiler.INSTANCE.setActive( false );
        Notification n = new Notification( "Status", this, ++notifications, "Enovia Profiler Stopped" );
        sendNotification( n );
    }

    public void report( String fileName )
    {
        Notification n;
        MRIReportWriter generator = new MRIReportWriter();
        try
        {
            generator.createReport( fileName );
            n = new Notification( "Status", this, ++notifications, "Report created in: " + fileName );
        }
        catch( FileNotFoundException e )
        {
            n = new Notification( "Error", this, ++notifications, e.getMessage() );
        }
        sendNotification( n );
    }
}