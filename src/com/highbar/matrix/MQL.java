package com.highbar.matrix;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class makes it easier to run commands in an mql session
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MQL
{
    private final Logger log = Logger.getLogger( getClass() );

    private MQLCommand mqlCommand;
    private Context context;

    /**
     * Creates a new MQL session 
     */
    public MQL()
    {
        this.mqlCommand = new MQLCommand();
        this.context = null;
    }

    /**
     * Creates a new MQL session for the given context.
     * @param context the ematrix database connection context
     */
    public MQL( Context context )
    {
        this.mqlCommand = new MQLCommand();
        this.context = context;
    }

    /**
     * Executes an MQL command using the context with which this instance was initialized
     * @param command the command to run
     * @return the output of the command
     * @throws MatrixException if the command fails
     * @throws MQLException if the
     */
    public String run( String command ) throws MatrixException
    {
        return run( context, command );
    }

    /**
     * Executes an MQL command using the supplied context
     * @param context the ematrix database connection context
     * @param command the command to execute
     * @return the command result
     * @throws MatrixException if execution of the command fails
     * @throws MQLException if the mql command returns an error
     */
    public String run( Context context, String command ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context + "|" + command ); }

        String result;

        if ( !mqlCommand.executeCommand( context, command ) )
        {
            String error = mqlCommand.getError();
            if ( error.length() != 0 )
            {
                error = error.substring( 0, error.length() - 1 );
            }
            throw new MQLException( "MQL command reported the following error: " + error );
        }
        result = mqlCommand.getResult();
        if ( result.length() != 0 )
        {
            result = result.substring( 0, result.length() - 1 );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + result ); }

        return result;
    }
}