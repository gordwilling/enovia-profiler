package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.jaxb.XTrigger;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Table to display trigger information
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class TriggersTableModel extends AbstractTableModel
{
    private List<XTrigger> triggers;

    public TriggersTableModel( List<XTrigger> triggers )
    {
        this.triggers = triggers;
    }

    public int getRowCount()
    {
        return triggers.size();
    }

    public int getColumnCount()
    {
        return 6;
    }

    public String getColumnName( int columnIndex )
    {
        switch( columnIndex )
        {
            case 0: return "Trigger Name";
            case 1: return "Trigger Revision";
            case 2: return "Sequence Number";
            case 3: return "Program";
            case 4: return "Method";
            case 5: return "# of Invocations";
            default: throw new IllegalArgumentException( "Triggers Table: Column Index out of Bounds" );
        }
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        switch( columnIndex )
        {
            case 0: return triggers.get( rowIndex ).getName();
            case 1: return triggers.get( rowIndex ).getRevision();
            case 2: return triggers.get( rowIndex ).getSequence();
            case 3: return triggers.get( rowIndex ).getProgram();
            case 4: return triggers.get( rowIndex ).getMethod();
            case 5: return triggers.get( rowIndex ).getInvocations();
            default: throw new IllegalArgumentException( "Triggers Table: Column Index out of Bounds" );
        }
    }
}