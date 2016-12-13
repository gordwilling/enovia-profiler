package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.jaxb.XMethodSummary;
import com.highbar.tools.mri.monitor.ExecutionNode;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   The table that displays the method invocation summary, including number of executions and total
 *   invocation time
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
class MethodSummaryTableModel  extends AbstractTableModel
{
    private final List<XMethodSummary> methodSummary;

    public MethodSummaryTableModel( List<XMethodSummary> methodSummary )
    {
        this.methodSummary = methodSummary;
    }

    public int getRowCount()
    {
        return methodSummary.size();
    }

    public int getColumnCount()
    {
        return 6;
    }

    public String getColumnName( int columnIndex )
    {
        switch ( columnIndex )
        {
            case 0:
                return "Type";
            case 1:
                return "Name";
            case 2:
                return "Method";
            case 3:
                return "Invocations";
            case 4:
                return "Total Time (ms)";
            case 5:
                return "Avg. Time (ms)";
            default:
                throw new IllegalArgumentException( "Main Summary Table: Column Index out of Bounds" );
        }
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        XMethodSummary m = methodSummary.get( rowIndex );
        switch ( columnIndex )
        {
            case 0:
                return ExecutionNode.Type.valueOf( m.getType() ).toDisplayString();
            case 1:
                return m.getName();
            case 2:
                return m.getMethod();
            case 3:
                return m.getInvocations();
            case 4:
                return NumberFormats.nanosToMillis( m.getTotalTime() );
            case 5:
                return NumberFormats.nanosToMillis( m.getAverageTime() );
            default:
                throw new IllegalArgumentException( "Main Summary Table: Column Index out of Bounds" );
        }
    }
}