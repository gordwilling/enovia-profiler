package com.highbar.tools.trigger.cache;

import com.highbar.matrix.schema.Types;

import java.util.Arrays;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class represents the 'eService Trigger Program Parameters' business object
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class TriggerProgramParameters implements Comparable<TriggerProgramParameters>
{
    private String name;
    private String revision;
    private String constructorArguments;
    private String methodName;
    private String programName;
    private String sequenceNumber;
    private String targetStates;
    private String errorType;
    private String[] programArguments = new String[15];
    private boolean isActive;
    private boolean isJava;

    public String getType()
    {
        return Types.eServiceTriggerProgramParameters.n();
    }

    public String getName()
    {
        return name;
    }

    void setName( String name )
    {
        this.name = name;
    }

    public String getRevision()
    {
        return revision;
    }

    void setRevision( String revision )
    {
        this.revision = revision;
    }

    public String getConstructorArguments()
    {
        return constructorArguments;
    }

    void setConstructorArguments( String constructorArguments )
    {
        this.constructorArguments = constructorArguments;
    }

    public String getMethodName()
    {
        return methodName;
    }

    void setMethodName( String methodName )
    {
        this.methodName = methodName;
    }

    public String getProgramName()
    {
        return programName;
    }

    void setProgramName( String programName )
    {
        this.programName = programName;
    }

    public String getSequenceNumber()
    {
        return sequenceNumber;
    }

    void setSequenceNumber( String sequenceNumber )
    {
        this.sequenceNumber = sequenceNumber;
    }

    public String getTargetStates()
    {
        return targetStates;
    }

    void setTargetStates( String targetStates )
    {
        this.targetStates = targetStates;
    }

    public String getErrorType()
    {
        return errorType;
    }

    void setErrorType( String errorType )
    {
        this.errorType = errorType;
    }

    public String[] getProgramArguments()
    {
        String[] defensiveCopy = new String[programArguments.length];
        System.arraycopy( programArguments, 0, defensiveCopy, 0, defensiveCopy.length );
        return defensiveCopy;
    }

    void setProgramArgument1( String programArgument1 )
    {
        this.programArguments[0] = programArgument1;
    }

    void setProgramArgument2( String programArgument2 )
    {
        this.programArguments[1] = programArgument2;
    }

    void setProgramArgument3( String programArgument3 )
    {
        this.programArguments[2] = programArgument3;
    }

    void setProgramArgument4( String programArgument4 )
    {
        this.programArguments[3] = programArgument4;
    }

    void setProgramArgument5( String programArgument5 )
    {
        this.programArguments[4] = programArgument5;
    }

    void setProgramArgument6( String programArgument6 )
    {
        this.programArguments[5] = programArgument6;
    }

    void setProgramArgument7( String programArgument7 )
    {
        this.programArguments[6] = programArgument7;
    }

    void setProgramArgument8( String programArgument8 )
    {
        this.programArguments[7] = programArgument8;
    }

    void setProgramArgument9( String programArgument9 )
    {
        this.programArguments[8] = programArgument9;
    }

    void setProgramArgument10( String programArgument10 )
    {
        this.programArguments[9] = programArgument10;
    }

    void setProgramArgument11( String programArgument11 )
    {
        this.programArguments[10] = programArgument11;
    }

    void setProgramArgument12( String programArgument12 )
    {
        this.programArguments[11] = programArgument12;
    }

    void setProgramArgument13( String programArgument13 )
    {
        this.programArguments[12] = programArgument13;
    }

    void setProgramArgument14( String programArgument14 )
    {
        this.programArguments[13] = programArgument14;
    }

    void setProgramArgument15( String programArgument15 )
    {
        this.programArguments[14] = programArgument15;
    }

    public boolean isActive()
    {
        return isActive;
    }

    void setActive( boolean isActive )
    {
        this.isActive = isActive;
    }

    public boolean isJava()
    {
        return isJava;
    }

    void setJava( boolean isJava )
    {
        this.isJava = isJava;
    }

    /**
     * Compares the name and sequence number of this object with another.  Given a list of <tt>TriggerProgramParameter</tt>
     * objects, simply sorting the list using this comparison algorithm should give the correct execution order. When
     * two sequence numbers are the same (which is actually a configuration error in the database) their relative
     * positions in the sorted list will not change (assuming you use Collections.sort() for the sorting).  This
     * means that objects should execute in the order they were pulled out of the database - the same behavior as
     * the current trigger manager implementation
     * @param that the other object to compare sequence numbers with
     * @return 0 if the sequence numbers are the same, -1 if this sequence number is
     *         less than that, 1 if this sequence number is greater than that
     */
    public int compareTo( TriggerProgramParameters that )
    {
        int result;

        Float thisSequence = null;
        Float thatSequence;

        try
        {
            thisSequence = Float.valueOf( this.getSequenceNumber() );
            thatSequence = Float.valueOf( that.getSequenceNumber() );

            result = this.getName().compareTo( that.getName() );

            if ( result == 0)
            {
                result = thisSequence.compareTo( thatSequence );
            }
        }
        catch( NumberFormatException e )
        {
            result = thisSequence == null ? -1 : 1;
        }

        return result;
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

        TriggerProgramParameters that = (TriggerProgramParameters)o;

        return name.equals( that.name ) && revision.equals( that.revision );
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return new StringBuilder( "TriggerProgramParameters{" )
                .append( "name='" )
                .append( name )
                .append( '\'' )
                .append( ", revision='" )
                .append( revision )
                .append( '\'' )
                .append( ", constructorArguments='" )
                .append( constructorArguments )
                .append( '\'' )
                .append( ", methodName='" )
                .append( methodName )
                .append( '\'' )
                .append( ", programName='" )
                .append( programName )
                .append( '\'' )
                .append( ", sequenceNumber='" )
                .append( sequenceNumber )
                .append( '\'' )
                .append( ", targetStates='" )
                .append( targetStates )
                .append( '\'' )
                .append( ", errorType='" )
                .append( errorType )
                .append( '\'' )
                .append( ", programArguments=" )
                .append( programArguments == null ? null : Arrays.asList( programArguments ) )
                .append( ", isActive=" )
                .append( isActive )
                .append( ", isJava=" )
                .append( isJava )
                .append( '}' )
                .toString();
    }
}