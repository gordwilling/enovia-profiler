package com.highbar.tools.trigger.findMissing;

import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 * <p/>
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class Trigger
{
    Triggerable triggerable;
    String state = "n/a";
    String event;
    String program;
    List<String> parameters;

    public Triggerable getTriggerable()
    {
        return triggerable;
    }

    public void setTriggerable( Triggerable triggerable )
    {
        this.triggerable = triggerable;
    }

    public String getState()
    {
        return state;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent( String event )
    {
        this.event = event;
    }

    public String getProgram()
    {
        return program;
    }

    public void setProgram( String program )
    {
        this.program = program;
    }

    public List<String> getParameters()
    {
        return parameters;
    }

    public void setParameters( List<String> parameters )
    {
        this.parameters = parameters;
    }

    @Override
    public String toString()
    {
        return "Trigger{" +
                "triggerable=" + triggerable +
                ", state='" + state + '\'' +
                ", event='" + event + '\'' +
                ", program='" + program + '\'' +
                ", parameters=" + parameters +
                '}';
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

        Trigger trigger = (Trigger)o;

        return event.equals( trigger.event ) &&
               parameters.equals( trigger.parameters ) &&
               program.equals( trigger.program ) &&
               state.equals( trigger.state ) &&
               triggerable.equals( trigger.triggerable );

    }

    @Override
    public int hashCode()
    {
        int result = triggerable.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + event.hashCode();
        result = 31 * result + program.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }
}
