package com.highbar.tools.mri.monitor;

/**
 * <dl>
* <dt><b>Description:</b>
* <dd>
*   This class holds information about a JPO invocation, specifically, the JPO name, the method name
 *  and the parameters values passed to the method
* </dd>
* </dt>
* <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
* </dl>
*
* @author Gordon Wallace - gw@highbar.com
* @version 1.0
*/
public class JPO
{
    private String name;
    private String method;
    private Object[] args;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod( String method )
    {
        this.method = method;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public void setArgs( Object[] args )
    {
        this.args = args;
    }
}
