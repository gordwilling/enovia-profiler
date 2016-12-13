package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.jaxb.XExecutionNode;
import com.highbar.tools.mri.monitor.ExecutionNode;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Table for displaying thread stack traces
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class ThreadStackTableModel extends AbstractTableModel
{
    List<XExecutionNode> executionNodes;

    public ThreadStackTableModel( List<XExecutionNode> executionNodes )
    {
        this.executionNodes = executionNodes;
    }

    public int getRowCount()
    {
        return executionNodes.size();
    }

    public int getColumnCount()
    {
        return 7;
    }

    public String getColumnName( int column )
    {
        switch ( column )
        {
            case 0: return "Order";
            case 1: return "Depth";
            case 2: return "Type";
            case 3: return "Name";
            case 4: return "Method";
            case 5: return "Time (ms)";
            case 6: return "Net Time (ms)";

            default: return null;
        }
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        XExecutionNode n = executionNodes.get( rowIndex );

        if ( ExecutionNode.Type.ROOT.toString().equals( n.getType() ) )
        {
            switch( columnIndex )
            {
                case 0: return "";
                case 1: return n.getName();
                case 5: return NumberFormats.nanosToMillis( n.getTotalTime() );
                default: return null;
            }
        }
        else
        {
            switch( columnIndex )
            {
                case 0: return n.getOrder();
                case 1: return getLevelString( n );
                case 2: return ExecutionNode.Type.valueOf( n.getType() ).toDisplayString();
                case 3: return n.getName();
                case 4: return n.getMethod();
                case 5: return NumberFormats.nanosToMillis( n.getTotalTime() );
                case 6: return NumberFormats.nanosToMillis( n.getAdjustedTime() );
                default: return null;
            }
        }
    }

    private String getLevelString( XExecutionNode node )
    {
        StringBuilder s = new StringBuilder();
        for ( int i = 0; i < node.getDepth(); i++ )
        {
            s.append( "    " );
        }
        s.append( ' ' );
        s.append( node.getDepth() );
        return s.toString();
    }
}
