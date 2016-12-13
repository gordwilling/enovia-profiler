package com.highbar.matrix;

import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.QueryIterator;
import matrix.util.MatrixException;

import java.util.Iterator;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Wrapper class enabling type-safe iteration over matrix query results using
 *   the java 1.5 foreach construct
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class TempQueryResults implements Iterable<BusinessObjectWithSelect>
{
    BusinessObjectWithSelectList list;
    QueryIterator iterator;

    TempQueryResults( BusinessObjectWithSelectList businessObjectWithSelectList )
    {
        this.list = businessObjectWithSelectList;
    }

    TempQueryResults( QueryIterator queryIterator )
    {
        this.iterator = queryIterator;
    }

    public Iterator<BusinessObjectWithSelect> iterator()
    {
        if ( this.list != null )
        {
            return getBusinessObjectWithSelectIterator( list );
        }
        else
        {
            return getQueryIterator( iterator );
        }
    }

    private Iterator<BusinessObjectWithSelect> getBusinessObjectWithSelectIterator(
            final BusinessObjectWithSelectList businessObjectWithSelectList )
    {
        return new Iterator<BusinessObjectWithSelect>() {

            Iterator i = businessObjectWithSelectList.iterator();
            public boolean hasNext()
            {
                return i.hasNext();
            }

            public BusinessObjectWithSelect next()
            {
                return (BusinessObjectWithSelect)i.next();
            }

            public void remove()
            {
                throw new UnsupportedOperationException( "This Iterator implementation does not support remove()" );
            }
        };
    }

    private Iterator<BusinessObjectWithSelect> getQueryIterator( final QueryIterator queryIterator )
    {
        return new Iterator<BusinessObjectWithSelect>() {

            public boolean hasNext()
            {
                return queryIterator.hasNext();
            }

            public BusinessObjectWithSelect next()
            {
                try
                {
                    return queryIterator.next();
                }
                catch( MatrixException e )
                {
                    throw new MatrixAccessException( e );
                }
            }

            public void remove()
            {
                throw new UnsupportedOperationException( "This Iterator implementation does not support remove()" );
            }
        };
    }
}