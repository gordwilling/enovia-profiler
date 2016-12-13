package com.highbar.tools.trigger.findMissing;

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
public class Triggerable
{
    String name;
    String type;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Triggerable{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
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

        Triggerable that = (Triggerable)o;

        return !( name != null ? !name.equals( that.name ) : that.name != null ) &&
               !( type != null ? !type.equals( that.type ) : that.type != null );

    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( type != null ? type.hashCode() : 0 );
        return result;
    }
}