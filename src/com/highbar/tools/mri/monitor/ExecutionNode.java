package com.highbar.tools.mri.monitor;

import com.highbar.util.DefaultStopWatch;
import com.highbar.util.StopWatch;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 * <p/>
 *   This class is used by the monitor to track information about a single method invocation, including
 *   the "category" (e.g. api, jsp, jpo, etc) target object name, method name, parameter values and execution time. 
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class ExecutionNode implements Comparable<ExecutionNode>
{
    /**
     * An enumeration of categories with which an execution node can be associated.
     */
    public enum Type 
    { 
        ROOT { public String toDisplayString() { return "Thread Root"; } },
        LG_CNS_API { public String toDisplayString() { return "LG CNS"; } }, 
        MX_API { public String toDisplayString() { return "Matrix API"; } }, 
        JSP { public String toDisplayString() { return "JSP"; } },
        JPO { public String toDisplayString() { return "JPO"; } }, 
        JAVA_TRIGGER { public String toDisplayString() { return "Trigger (Java)"; } }, 
        TCL_TRIGGER { public String toDisplayString() { return "Trigger (TCL)"; } }, 
        TRIGGER_MANAGER { public String toDisplayString() { return "Trigger Manager"; } },

        /**
         *@deprecated use either {@link #LG_CNS_API} or {@link #MX_API} instead
         */
        API { public String toDisplayString() { return "Matrix API"; } },

        /**
         *@deprecated use either {@link #TCL_TRIGGER} or {@link #JAVA_TRIGGER} instead
         */
        TRIGGER { public String toDisplayString() { return "Trigger (Java)"; } },;
        
        public abstract String toDisplayString();
    }

    private Type type;
    private String name;
    private String method;
    private Object[] parameters;
    private StopWatch stopWatch;
    private String threadId;
    private long netTime;

    public ExecutionNode( Type type, JPO jpo )
    {
        this( type, jpo.getName(), jpo.getMethod(), jpo.getArgs() );
    }

    public ExecutionNode( Type type, String name, String method, Object[] parameters )
    {
        this.type = type;
        this.name = name;
        this.method = method;
        this.parameters = parameters;
        this.stopWatch = new DefaultStopWatch();
        this.threadId = Thread.currentThread().toString();
    }

    public Type getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod( String method )
    {
        this.method = method;
    }

    public Object[] getParameters()
    {
        return parameters;
    }

    public void setParameters( Object[] parameters )
    {
        this.parameters = parameters;
    }

    public StopWatch getStopWatch()
    {
        return stopWatch;
    }

    public String getThreadId()
    {
        return threadId;
    }

    public long getNetTime()
    {
        return netTime;
    }

    public void setNetTime( long netTime )
    {
        this.netTime = netTime;
    }

    public int compareTo( ExecutionNode that )
    {
        int returnValue = 0;

        if ( that == null )
        {
            returnValue = 1;
        }

        if ( returnValue == 0 )
        {
            returnValue = this.getType().compareTo( that.getType() );
        }

        if ( returnValue == 0 )
        {
            returnValue = this.getName().compareTo( that.getName() );
        }

        if ( returnValue == 0 )
        {
            returnValue = this.getMethod().compareTo( that.getMethod() );
        }

        return returnValue;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ExecutionNode that = (ExecutionNode)o;

        return !( method != null ? !method.equals( that.method ) : that.method != null )
                && !( name != null ? !name.equals( that.name ) : that.name != null )
                && type == that.type;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( method != null ? method.hashCode() : 0 );
        return result;
    }
}
