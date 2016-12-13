package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.jaxb.XMethodParameter;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   The table that shows parameter values passed to a selected method
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MethodParametersTableModel  extends AbstractTableModel
{
    List<XMethodParameter> methodParameters;

    public MethodParametersTableModel( List<XMethodParameter> methodParameters )
    {
        this.methodParameters = methodParameters;
    }

    public int getRowCount()
    {
        return methodParameters.size();
    }

    public int getColumnCount()
    {
        return 2;
    }

    public String getColumnName( int columnIndex )
    {
        switch( columnIndex )
        {
            case 0: return "Argument Type";
            case 1: return "Argument Value";
            default: throw new IllegalArgumentException( "Invalid Table Column Specified" );
        }
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        switch( columnIndex )
        {
            case 0: return methodParameters.get( rowIndex ).getType();
            case 1: return methodParameters.get( rowIndex ).getValue();
            default: throw new IllegalArgumentException( "Invalid Table Column Specified" );
        }
    }


    public void setData( List<XMethodParameter> methodParameters )
    {
        this.methodParameters = methodParameters;    
    }
}