package com.highbar.tools.trigger.findMissing;

import com.highbar.matrix.MQL;
import com.highbar.matrix.MatrixContext;
import com.highbar.matrix.MatrixTransaction;
import com.highbar.tools.trigger.cache.TriggerProgramParameters;
import com.highbar.tools.trigger.cache.TriggerProgramParametersCache;
import matrix.util.MatrixException;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class generates a report showing Triggers that have no corresponding eService Trigger Program Parameter
 *   object.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MissingTriggerFinder
{
    private MQL mql;
    private MatrixContext context;

    public MissingTriggerFinder()
    {
        context = new MatrixContext( "creator", "" );
        mql = new MQL( context.getContext() );
    }

    public static void main( String... args )
    {
        MissingTriggerFinder missingTriggerFinder = new MissingTriggerFinder();
        try
        {
            missingTriggerFinder.run();
        }
        catch( MatrixException e )
        {
            e.printStackTrace();
        }
    }

    private void run() throws MatrixException
    {
        context.connect();

        try
        {
            showMissingTriggers();
        }
        finally
        {
            context.disconnect();
        }
    }


    /**
     * Outputs trigger definitions which reference 'eService Trigger Program Parameters' objects that don't exist.
     * @throws MatrixException in the event of an unknown error
     */
    private void showMissingTriggers() throws MatrixException
    {
        MatrixTransaction transaction = context.startWriteTransaction();

        try
        {
            List<Trigger> triggers = getTriggers( false );

            System.out.println( "Admin Type\tName\tState\tTrigger Event\tTrigger Program\tTrigger Program Parameters" );

            List<String> output = new ArrayList<String>();

            for ( Trigger trigger : triggers )
            {
                for ( String programParametersName : trigger.getParameters() )
                {
                    List<TriggerProgramParameters> l = TriggerProgramParametersCache.INSTANCE.getTriggerProgramParameters(
                            context.getContext(), programParametersName );

                    if ( l == null )
                    {
                        String s = new StringBuilder()
                            .append( trigger.getTriggerable().getType() )
                            .append( '\t' )
                            .append( trigger.getTriggerable().getName() )
                            .append( '\t' )
                            .append( trigger.getState() )
                            .append( '\t' )
                            .append( trigger.getEvent() )
                            .append( '\t' )
                            .append( trigger.getProgram() )
                            .append( '\t' )
                            .append( programParametersName )
                            .toString();

                        output.add( s );
                    }
                }
            }

            for( String s : output )
            {
                System.out.println( s );
            }

            System.out.println( "" + output.size() +  " references to non-existent trigger program parameters" );

            transaction.commit();
        }
        catch( MatrixException e )
        {
            transaction.abort();
            throw e;
        }
    }

    /**
     * Gets a list of all triggers defined in the system.  Triggers can exist on attributes, relationships, types
     * and states
     * @param includeInherited true to include inherited triggers for all types and relationships, false to show
     *        only immediate triggers
     * @return the list of all triggers defined in the system.
     * @throws MatrixException in the event of an unknown error
     */
    private List<Trigger> getTriggers( boolean includeInherited ) throws MatrixException
    {
        List<Trigger> triggers = new ArrayList<Trigger>();

        Triggerable triggerable = null;

        for ( String adminType : getTriggerableTypeNames( includeInherited ).keySet() )
        {
            String command =  "list '" + adminType + "' * select " + getTriggerableTypeNames( includeInherited ).get( adminType );
            System.out.println( "<mql>" + command );

            String s = mql.run( command);

            for( String token : Arrays.asList( StringUtils.split( s, "\n" ) ) )
            {
                if ( token.startsWith( "business type" ) ||
                        token.startsWith( "relationship type" ) ||
                        token.startsWith( "attribute type" ) )
                {
                    triggerable = getTypeRelationshipOrAttributeTriggerable( token );
                }
                else if ( token.startsWith( "policy") )
                {
                    triggerable = getPolicyTriggerable( token );
                }
                else if ( token.indexOf( "state[" ) != -1 )
                {
                    String[] triggerInfo = StringUtils.split( token, "[]=:()" );

                    Trigger tc = getStateTrigger( triggerInfo );
                    tc.setTriggerable( triggerable );
                    triggers.add( tc );
                }
                else if ( token.indexOf( "trigger = ") != -1 )
                {
                    String[] triggerInfo = StringUtils.split( token, "=:()" );

                    Trigger tc = getTypeRelationshipOrAttributeTrigger( triggerInfo );
                    tc.setTriggerable( triggerable );
                    triggers.add( tc );
                }
            }
        }

        return triggers;
    }

    private Triggerable getTypeRelationshipOrAttributeTriggerable( String token )
    {
        Triggerable triggerable = new Triggerable();
        String[] type = StringUtils.split( token, " " );
        triggerable.setType( type[0].equals( "business" ) ? "type" : type[0] );
        triggerable.setName( getNameFromRemainingTokens( type, 2 ) );

        return triggerable;
    }

    private Triggerable getPolicyTriggerable( String token )
    {
        Triggerable triggerable = new Triggerable();
        String[] policy = StringUtils.split( token, " " );
        triggerable.setType( policy[0] );
        triggerable.setName( getNameFromRemainingTokens( policy, 1 ) );

        return triggerable;
    }

    private Trigger getStateTrigger( String[] triggerInfo )
    {
        Trigger tc = new Trigger();

        tc.setState( triggerInfo[1] );
        tc.setEvent( triggerInfo[3].trim() );
        tc.setProgram( triggerInfo[4] );

        if ( triggerInfo.length > 5 )
        {
            tc.setParameters( Arrays.asList( StringUtils.split( triggerInfo[5], " " ) ) );
        }
        else
        {
            tc.setParameters( new ArrayList<String>() );
        }
        return tc;
    }

    private Trigger getTypeRelationshipOrAttributeTrigger( String[] triggerInfo )
    {
        Trigger t = new Trigger();
        t.setEvent( triggerInfo[1].trim() );
        t.setProgram( triggerInfo[2] );
        if ( triggerInfo.length > 3 )
        {
            t.setParameters( Arrays.asList( StringUtils.split( triggerInfo[3], " " ) ) );
        }
        else
        {
            // blank parameters for trigger program (which is still bad
            List<String> blank = new ArrayList<String>();
            blank.add( "" );
            t.setParameters( blank );
        }
        return t;
    }

    private String getNameFromRemainingTokens( String[] type, int startIndex )
    {
        StringBuilder name = new StringBuilder( type[startIndex++] );
        for ( int i = startIndex; i < type.length; i++ )
        {
            name.append( ' ' ).append( type[i] );
        }
        return name.toString();
    }

    public Map<String,String> getTriggerableTypeNames( boolean includeInherited )
    {
        Map<String,String> m = new TreeMap<String,String>();
        m.put( "type", includeInherited ? "trigger" : "immediatetrigger" );
        m.put( "relationship", includeInherited ? "trigger" : "immediatetrigger" );
        m.put( "attribute", "trigger" );
        m.put( "policy", "state.trigger" );

        return m;
    }
}