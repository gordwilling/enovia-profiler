package com.highbar.matrix;

import matrix.db.BusinessObjectWithSelect;
import matrix.db.ExpansionIterator;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.util.MatrixException;

import java.util.Iterator;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Wrapper class enabling type-safe iteration over matrix expansion results using
 *   the java 1.5 foreach construct
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class ExpandResults implements Iterable<RelationshipWithSelect>
{
    private ExpansionWithSelect expansionWithSelect;
    private ExpansionIterator expansionIterator;

    ExpandResults( ExpansionWithSelect expansionWithSelect )
    {
        this.expansionWithSelect = expansionWithSelect;
    }

    ExpandResults( ExpansionIterator expansionIterator )
    {
        this.expansionIterator = expansionIterator;
    }

    /**
     * Gets the BusinessObjectWithSelect at the root of the expansion.  If result paging was used in the
     * expand call, the root object is not available, and an IllegalStateException will be thrown
     * @return the BusinessObjectWithSelect, populated with the bus selectables retrieved during the expand
     * @throws IllegalStateException if the expand results were paged
     */
    public BusinessObjectWithSelect getRootWithSelect()
    {
        if ( expansionWithSelect != null )
        {
            return expansionWithSelect.getRootWithSelect();
        }
        else
        {
            // expansionIterator doesn't expose a reference to the root object
            throw new IllegalStateException( "Root Object not available when using paged expand results." );
        }
    }

    /**
     * Get a RelationshipWithSelect iterator for the expansion results.  The root object in the expand is not
     * exposed by this iterator.
     * @return the iterator for the expansion results
     */
    public Iterator<RelationshipWithSelect> iterator()
    {
        return expansionWithSelect != null ? getRelationshipWithSelectIterator( expansionWithSelect ) :
                getExpansionIterator( expansionIterator );
    }

    private Iterator<RelationshipWithSelect> getRelationshipWithSelectIterator(
            final ExpansionWithSelect expansionWithSelect )
    {
        return new Iterator<RelationshipWithSelect>() {

            Iterator i = expansionWithSelect.getRelationships().iterator();

            public boolean hasNext()
            {
                return i.hasNext();
            }

            public RelationshipWithSelect next()
            {
                return (RelationshipWithSelect)i.next();
            }

            public void remove()
            {
                throw new UnsupportedOperationException( "This Iterator implementation does not support remove()" );
            }
        };
    }

    private Iterator<RelationshipWithSelect> getExpansionIterator( final ExpansionIterator expansionIterator )
    {
        return new Iterator<RelationshipWithSelect>() {

            public boolean hasNext()
            {
                return expansionIterator.hasNext();
            }

            public RelationshipWithSelect next()
            {
                try
                {
                    return expansionIterator.next();
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