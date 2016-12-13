package com.highbar.tools.mri.report;

import com.highbar.tools.mri.jaxb.*;
import com.highbar.tools.mri.monitor.ExecutionNode;
import com.highbar.tools.mri.monitor.MRIProfiler;
import com.highbar.tools.trigger.cache.TriggerProgramParameters;
import com.highbar.util.StopWatch;
import com.highbar.util.TreeNode;
import matrix.db.Context;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class generates an xml file containing information gathered by the MRIProfiler
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MRIReportWriter
{
    /**
     * Generates a profile report with the specified file name
     * @param outputFileName the name of the report file to generate
     * @throws FileNotFoundException if the report file cannot be created
     */
    public void createReport( String outputFileName ) throws FileNotFoundException
    {
        PrintWriter out = new PrintWriter( outputFileName );
        try
        {
            Set<TreeNode<ExecutionNode>> rootExecutionNodes = MRIProfiler.INSTANCE.getRootExecutionNodes();
            Map<ExecutionNode, List<ExecutionNode>> executionNodeMap = MRIProfiler.INSTANCE.getExecutionNodeMap();
            List<StopWatch> triggerOverhead = MRIProfiler.INSTANCE.getTriggerManagerOverhead();
            Map<String, Integer> missingTriggers = MRIProfiler.INSTANCE.getMissingTriggers();
            Map<TriggerProgramParameters, Integer> triggers = MRIProfiler.INSTANCE.getTriggers();

            setAdjustedTimes( rootExecutionNodes );

            XEnoviaProfilerReport report = new XEnoviaProfilerReport();
            report.setOverhead( getTriggerManagerOverhead( triggerOverhead ) );
            report.getActiveTriggers().addAll( getTriggers( triggers, true ) );
            report.getInactiveTriggers().addAll( getTriggers( triggers, false ) );
            report.getMissingTriggers().addAll( getMissingTriggers( missingTriggers ) );
            report.getMethodSummary().addAll( getMethodSummary( executionNodeMap ) );

            for ( TreeNode<ExecutionNode> root : rootExecutionNodes )
            {
                report.getStackTraces().add( getStackTrace( root ) );
            }

            JAXBContext context = JAXBContext.newInstance( XEnoviaProfilerReport.class );
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(report, out);
        }
        catch( Exception e )
        {
            e.printStackTrace(); 
        }
        finally
        {
            out.close();
        }    
    }

    private List<XMissingTrigger> getMissingTriggers( Map<String, Integer> missingTriggers )
    {
        List<XMissingTrigger> list = new ArrayList<XMissingTrigger>();

        for ( Map.Entry<String, Integer> entry : missingTriggers.entrySet() )
        {
            XMissingTrigger t = new XMissingTrigger();
            t.setName( entry.getKey() );
            t.setAttempts( entry.getValue() );
            list.add( t );
        }

        return list;
    }

    private List<XTrigger> getTriggers( Map<TriggerProgramParameters, Integer> triggers, boolean active )
    {
        List<XTrigger> list = new ArrayList<XTrigger>();

        for ( Map.Entry<TriggerProgramParameters,Integer> entry : triggers.entrySet() )
        {
            TriggerProgramParameters trigger = entry.getKey();

            if ( trigger.isActive() == active )
            {
                XTrigger t = new XTrigger();
                t.setName( trigger.getName() );
                t.setRevision( trigger.getRevision() );
                t.setProgram( trigger.getProgramName() );
                t.setMethod( trigger.getMethodName() );
                t.setSequence( trigger.getSequenceNumber() );
                t.setInvocations( entry.getValue() );

                list.add( t );
            }
        }

        return list;
    }

    private XStackTrace getStackTrace( TreeNode<ExecutionNode> rootExecutionNode )
    {
        XStackTrace stackTrace = new XStackTrace();
        stackTrace.setThreadId( rootExecutionNode.getObject().getThreadId() );
        stackTrace.setTotalTime( getTotalTimeForThread( rootExecutionNode ));

        int order = 0;
        for ( TreeNode<ExecutionNode> node : rootExecutionNode )
        {
            if ( node.isRoot() )
            {
                continue;
            }

            ExecutionNode executionNode = node.getObject();

            XExecutionNode x = new XExecutionNode();
            x.setOrder( order++ );
            x.setDepth( node.getLevel() );
            x.setType( executionNode.getType().toString() );
            x.setName( executionNode.getName() );
            x.setMethod( executionNode.getMethod() );
            x.setTotalTime( executionNode.getStopWatch().getElapsedTime() );
            x.setAdjustedTime( executionNode.getNetTime() );

            if ( executionNode.getParameters() != null && executionNode.getParameters().length != 0 )
            {
                String[] parameterTypes = getParameterTypes( executionNode.getMethod() );
                Object[] parameterValues = executionNode.getParameters();

                if ( parameterTypes != null && parameterValues != null && parameterTypes.length == parameterValues.length )
                {
                    for( int i = 0; i < parameterTypes.length; i++ )
                    {
                        XMethodParameter parameter = new XMethodParameter();
                        parameter.setType( parameterTypes[i] );
                        parameter.setValue( toXMLFriendly( getStringValue( parameterValues[i] ) ) );
                        x.getMethodParameters().add( parameter );
                    }
                }
                else
                {   
                    XMethodParameter parameter = new XMethodParameter();
                    parameter.setType( "[Unable to Retrieve]" );
                    parameter.setValue( "[Unable to Retrieve]" );
                    x.getMethodParameters().add( parameter );
                }
            }

            stackTrace.getExecutionNodes().add( x );
        }

        return stackTrace;
    }

    private String[] getParameterTypes( String methodSignature )
    {
        int start = methodSignature.indexOf( '(' );
        int end = methodSignature.lastIndexOf( ')' );

        if ( start != -1 && end != -1 )
        {
            String parameterTypesCSV = methodSignature.substring( methodSignature.indexOf( '(' ) + 1,
                    methodSignature.lastIndexOf( ')' ) );
            return parameterTypesCSV.split( ", " );
        }
        else
        {
            return null;
        }
    }

    private List<XMethodSummary> getMethodSummary( Map<ExecutionNode, List<ExecutionNode>> executionNodeMap )
    {
        List<XMethodSummary> summaryList = new ArrayList<XMethodSummary>();

        Set<ExecutionNode> sortedExecutionNodes = getSortedKeySet( executionNodeMap );

        for ( ExecutionNode executionNode : sortedExecutionNodes )
        {
            List<ExecutionNode> nodes = executionNodeMap.get( executionNode );

            XMethodSummary summary = new XMethodSummary();
            summary.setType( executionNode.getType().toString() );
            summary.setName( executionNode.getName() );
            summary.setMethod( executionNode.getMethod() );
            summary.setInvocations( nodes.size() );
            summary.setAverageTime( getAverageTime( nodes ) );
            summary.setTotalTime( getTotalTime( nodes ) );

            summaryList.add( summary );
        }

        return summaryList;
    }

    private XTriggerManagerOverhead getTriggerManagerOverhead( List<StopWatch> triggerOverhead )
    {
        XTriggerManagerOverhead xOverhead = new XTriggerManagerOverhead();

        for ( StopWatch stopWatch : triggerOverhead )
        {
            xOverhead.getTimings().add( stopWatch.getElapsedTime() );
        }

        return xOverhead;
    }

    private Set<ExecutionNode> getSortedKeySet( final Map<ExecutionNode, List<ExecutionNode>> executionNodeMap )
    {
        Set<ExecutionNode> sortedKeySet = new TreeSet<ExecutionNode>( new Comparator<ExecutionNode>() {

            public int compare( ExecutionNode one, ExecutionNode two )
            {
                long time = getTotalTime( executionNodeMap.get( two ) ) - getTotalTime( executionNodeMap.get( one ) );

                if ( time == 0 )
                {
                    return 0;
                }
                else if ( time > 0 )
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        });

        sortedKeySet.addAll( executionNodeMap.keySet() );

        return sortedKeySet;
    }

    private void setAdjustedTimes( Set<TreeNode<ExecutionNode>> rootExecutionNodes )
    {
        for ( TreeNode<ExecutionNode> rootNode : rootExecutionNodes )
        {
            for ( TreeNode<ExecutionNode> node : rootNode )
            {
                setAdjustedTime( node );
            }
        }
    }

    private void setAdjustedTime( TreeNode<ExecutionNode> node )
    {
        long t = node.getObject().getStopWatch().getElapsedTime();

        for ( TreeNode<ExecutionNode> child : node.getChildren() )
        {
            t -= child.getObject().getStopWatch().getElapsedTime();
        }
        node.getObject().setNetTime( t );
    }

    private long getTotalTimeForThread( TreeNode<ExecutionNode> rootNode )
    {
        long t = 0;
        for ( TreeNode<ExecutionNode> child : rootNode.getChildren() )
        {
            t += child.getObject().getStopWatch().getElapsedTime();
        }
        return t;
    }

    private long getTotalTime( List<ExecutionNode> nodes )
    {
        long t = 0;
        for ( ExecutionNode node : nodes )
        {
            t += node.getNetTime();
        }
        return t;
    }

    private long getAverageTime( List<ExecutionNode> nodes )
    {
        return getTotalTime( nodes ) / nodes.size();
    }

    private String toXMLFriendly( String s )
    {
        char[] chars = s.toCharArray();
        for ( int i = 0; i < chars.length; i++ )
        {
            if ( !isValidXML( chars[i] ) )
            {
                chars[i] = ' ';
            }
        }
        return new String( chars );
    }

    private static boolean isValidXML( char c )
    {
        // there's a real function out there that will do a better job at this (because there are many
        // other characters that are illegal) but this is the only character I've encountered so far
        // so I'm leaving it like this for now.
        return c != 0x07;
    }

    private String  getStringValue( Object o )
    {
        StringBuilder s = new StringBuilder();

        if ( o instanceof Object[] )
        {
            s.append( Arrays.toString( (Object[])o ) );
        }
        else if ( o instanceof int[] )
        {
            s.append( Arrays.toString( (int[])o ) );
        }
        else if ( o instanceof long[] )
        {
            s.append( Arrays.toString( (long[])o) );
        }
        else if ( o instanceof short[] )
        {
            s.append( Arrays.toString( (short[])o ) );
        }
        else if ( o instanceof char[] )
        {
            s.append( Arrays.toString( (char[])o ) );
        }
        else if ( o instanceof byte[] )
        {
            s.append( Arrays.toString( (byte[])o ) );
        }
        else if ( o instanceof boolean[] )
        {
            s.append( Arrays.toString( (boolean[])o ) );
        }
        else if ( o instanceof float[] )
        {
            s.append( Arrays.toString( (float[])o ) );
        }
        else if ( o instanceof double[] )
        {
            s.append( Arrays.toString( (double[])o ) );
        }
        else if ( o instanceof matrix.db.Context )
        {
            s.append( "Context (user: " ).append( ((Context)o).getUser() ).append( ')' );
        }
        else // some other object
        {
            s.append( o );
        }

        return s.toString();
    }
}