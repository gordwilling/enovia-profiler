package com.highbar.tools.trigger.cache;

import com.highbar.matrix.MQL;
import com.highbar.matrix.MatrixAccessException;
import com.highbar.matrix.TempQuery;
import com.highbar.matrix.TempQueryResults;
import com.highbar.matrix.schema.Attributes;
import com.highbar.matrix.schema.Basics;
import com.highbar.matrix.schema.Types;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.util.MatrixException;

import java.util.*;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Global cache for Trigger Program Parameter Objects
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum TriggerProgramParametersCache
{
    INSTANCE;

    private final Map<String, List<TriggerProgramParameters>> cache =
            Collections.synchronizedMap( new HashMap<String, List<TriggerProgramParameters>>() );

    /**
     * Gets the list of 'eService Trigger Program Parameters' objects with the specified name.  Items in the
     * returned list are arranged in their execution order (the first trigger that should execute is first in the
     * list, etc.)
     * @param context the ematrix database connection context
     * @param name the name of the eService Trigger Program Parameters objects to retrieve
     * @return the list of trigger parameter objects with the supplied name
     */
    public List<TriggerProgramParameters> getTriggerProgramParameters( Context context, String name )
    {
        synchronized( cache )
        {
            if ( cache.isEmpty() )
            {
                try
                {
                    initialize( context );
                }
                catch( MatrixException e )
                {
                    throw new MatrixAccessException( e );
                }
            }
        }

        List<TriggerProgramParameters> triggers = cache.get( name );

        if ( triggers != null )
        {
            // return a defensive copy
            triggers = new ArrayList<TriggerProgramParameters>( triggers );
        }

        return triggers;
    }

    /**
     * Clears the cache.  The cache will be rebuilt on the next call to {@link #getTriggerProgramParameters}
     */
    public void reset()
    {
        cache.clear();
    }

    private void initialize( Context context ) throws MatrixException
    {
        MQL mql = new MQL( context );
        TempQuery q = getQuery();
        TempQueryResults r = q.run( context );

        for ( BusinessObjectWithSelect b : r )
        {
            TriggerProgramParameters p = getTriggerProgramParameters( b );
            setProgramType( mql, p );
            addToCache( p );
        }

        sortCachedListsBySequenceNumber();
    }

    private void sortCachedListsBySequenceNumber()
    {
        for ( Map.Entry<String, List<TriggerProgramParameters>> stringListEntry : cache.entrySet() )
        {
            List<TriggerProgramParameters> list = stringListEntry.getValue();
            Collections.sort( list );
        }
    }

    private void setProgramType( MQL mql, TriggerProgramParameters p ) throws MatrixException
    {
        boolean programExists = mql.run( "list program '" + p.getProgramName() + '\'' ).length() != 0;

        if ( programExists )
        {
            String result = mql.run( "print program '" + p.getProgramName() + "' select isjavaprogram dump" );
            p.setJava( "TRUE".equalsIgnoreCase( result.trim() ) );
        }

        if ( p.isJava() && p.getMethodName().trim().length() == 0 )
        {
            p.setMethodName( "mxMain" );
        }
    }

    private void addToCache( TriggerProgramParameters p )
    {
        List<TriggerProgramParameters> list = cache.get( p.getName() );
        if ( list == null )
        {
            list = new ArrayList<TriggerProgramParameters>();
            cache.put( p.getName(), list );
        }
        list.add( p );
    }

    private TempQuery getQuery()
    {
        return new TempQuery()
                .type( Types.eServiceTriggerProgramParameters )
                .select( Basics.name, 
                        Basics.revision,
                        Basics.current,
                        Attributes.eServiceConstructorArguments,
                        Attributes.eServiceProgramName,
                        Attributes.eServiceMethodName,
                        Attributes.eServiceSequenceNumber,
                        Attributes.eServiceTargetStates,
                        Attributes.eServiceErrorType,
                        Attributes.eServiceProgramArgument1,
                        Attributes.eServiceProgramArgument2,
                        Attributes.eServiceProgramArgument3,
                        Attributes.eServiceProgramArgument4,
                        Attributes.eServiceProgramArgument5,
                        Attributes.eServiceProgramArgument6,
                        Attributes.eServiceProgramArgument7,
                        Attributes.eServiceProgramArgument8,
                        Attributes.eServiceProgramArgument9,
                        Attributes.eServiceProgramArgument10,
                        Attributes.eServiceProgramArgument11,
                        Attributes.eServiceProgramArgument12,
                        Attributes.eServiceProgramArgument13,
                        Attributes.eServiceProgramArgument14,
                        Attributes.eServiceProgramArgument15 );
    }

    private TriggerProgramParameters getTriggerProgramParameters( BusinessObjectWithSelect b )
    {
        TriggerProgramParameters p = new TriggerProgramParameters();

        p.setName( b.getSelectData( Basics.name.s() ) );
        p.setRevision( b.getSelectData( Basics.revision.s() ) );
        p.setConstructorArguments( b.getSelectData( Attributes.eServiceConstructorArguments.s() ) );
        p.setProgramName( b.getSelectData( Attributes.eServiceProgramName.s() ) );
        p.setMethodName( b.getSelectData( Attributes.eServiceMethodName.s() ) );
        p.setSequenceNumber( b.getSelectData( Attributes.eServiceSequenceNumber.s() ) );
        p.setTargetStates( b.getSelectData( Attributes.eServiceTargetStates.s() ) );
        p.setErrorType( b.getSelectData( Attributes.eServiceErrorType.s() ) );
        p.setProgramArgument1( b.getSelectData( Attributes.eServiceProgramArgument1.s() ) );
        p.setProgramArgument2( b.getSelectData( Attributes.eServiceProgramArgument2.s() ) );
        p.setProgramArgument3( b.getSelectData( Attributes.eServiceProgramArgument3.s() ) );
        p.setProgramArgument4( b.getSelectData( Attributes.eServiceProgramArgument4.s() ) );
        p.setProgramArgument5( b.getSelectData( Attributes.eServiceProgramArgument5.s() ) );
        p.setProgramArgument6( b.getSelectData( Attributes.eServiceProgramArgument6.s() ) );
        p.setProgramArgument7( b.getSelectData( Attributes.eServiceProgramArgument7.s() ) );
        p.setProgramArgument8( b.getSelectData( Attributes.eServiceProgramArgument8.s() ) );
        p.setProgramArgument9( b.getSelectData( Attributes.eServiceProgramArgument9.s() ) );
        p.setProgramArgument10( b.getSelectData( Attributes.eServiceProgramArgument10.s() ) );
        p.setProgramArgument11( b.getSelectData( Attributes.eServiceProgramArgument11.s() ) );
        p.setProgramArgument12( b.getSelectData( Attributes.eServiceProgramArgument12.s() ) );
        p.setProgramArgument13( b.getSelectData( Attributes.eServiceProgramArgument13.s() ) );
        p.setProgramArgument14( b.getSelectData( Attributes.eServiceProgramArgument14.s() ) );
        p.setProgramArgument15( b.getSelectData( Attributes.eServiceProgramArgument15.s() ) );

        // for some reason, the "Error Type" value affects whether or not the trigger is run as well as
        // the current state (based on logic seen in the Trigger Manager JPO)
        p.setActive( "Active".equals( b.getSelectData( Basics.current.s() ) )
                && "Error".equalsIgnoreCase( p.getErrorType() ) );

        return p;
    }
}