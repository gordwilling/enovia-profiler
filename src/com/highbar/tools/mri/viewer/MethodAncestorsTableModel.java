package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.jaxb.XExecutionNode;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   The table that shows all ancestors for a selected method
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MethodAncestorsTableModel extends AbstractTableModel
{
    private List<XExecutionNode> methodAncestors;

    public MethodAncestorsTableModel( List<XExecutionNode> methodAncestors )
    {
        this.methodAncestors = methodAncestors;
    }

    public int getRowCount()
    {
        return methodAncestors.size();
    }

    public int getColumnCount()
    {
        return 1;
    }

    public String getColumnName( int columnIndex )
    {
        return "Method Ancestors";
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        return methodAncestors.get( rowIndex );
    }

    public void setData( List<XExecutionNode> methodAncestors )
    {
        this.methodAncestors = methodAncestors;  
    }
}