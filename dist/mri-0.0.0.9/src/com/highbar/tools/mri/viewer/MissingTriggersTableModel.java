package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.jaxb.XMissingTrigger;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   The table that display missing triggers
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MissingTriggersTableModel  extends AbstractTableModel
{
    private List<XMissingTrigger> missingTriggers;

    public MissingTriggersTableModel( List<XMissingTrigger> missingTriggers )
    {
        this.missingTriggers = missingTriggers;
    }

    public int getRowCount()
    {
        return missingTriggers.size();
    }

    public int getColumnCount()
    {
        return 2;
    }

    public String getColumnName( int columnIndex )
    {
        switch( columnIndex )
        {
            case 0: return "Name";
            case 1: return "Invocation Attempts";
            default: throw new IllegalArgumentException( "Invalid Table Column Specified" );
        }
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        switch( columnIndex )
        {
            case 0: return missingTriggers.get( rowIndex ).getName();
            case 1: return missingTriggers.get( rowIndex ).getAttempts();
            default: throw new IllegalArgumentException( "Invalid Table Column Specified" );
        }
    }
}
