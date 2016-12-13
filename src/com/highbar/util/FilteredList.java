package com.highbar.util;

import java.util.AbstractList;
import java.util.List;
import java.util.ArrayList;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   List implementation that enables custom content filtering
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class FilteredList<T> extends AbstractList<T> implements FilterModifiedListener
{
    private List<T> unfilteredList;
    private List<T> filteredList;
    private Filter<T> filter;

    /**
     * Creates a filtered view of the specified list using the given filter
     * @param list the list for which the filter is to be applied
     * @param filter the filter implementation
     */
    public FilteredList( List<T> list, Filter<T> filter )
    {
        this.unfilteredList = list;
        this.filter = filter;
        this.filteredList = new ArrayList<T>();
        onFilterModify();
    }

    @Override
    public T get( int index )
    {
        return filteredList.get( index );
    }

    @Override
    public int size()
    {
        return filteredList.size();
    }

    /**
     * This method updates the view to the underlying list if the filter criteria changes
     */
    public void onFilterModify()
    {
        filteredList.clear();
        for ( T element : unfilteredList )
        {
            if ( filter.accept( element ) )
            {
                filteredList.add( element );
            }
        }
    }
}