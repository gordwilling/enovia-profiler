package com.highbar.matrix;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class is used to control transactions in Matrix.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MatrixTransaction
{
    private Context context;

    MatrixTransaction( MatrixContext context )
    {
        this.context = context.getContext();
    }

    /**
     * Aborts the current transaction
     */
    public void abort()
    {
        try
        {
            context.abort();
        }
        catch( MatrixException e )
        {
            throw new MatrixAccessException( e );
        }
    }

    /**
     * Commits the current transaction
     * @throws MatrixException if the commit fails for some reason
     */
    public void commit() throws MatrixException
    {
        context.commit();
    }
}