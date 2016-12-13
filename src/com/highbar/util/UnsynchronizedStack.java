package com.highbar.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * <dl>
 *   <dt><b>Description:</b>
 *     <dd>
 *       This stack differs from the JDK implementation in that it
 *       uses an unsynchronized List as its underlying data structure
 *       rather than a Vector, and exposes only exposes stack-specific
 *       operations
 *     </dd>
 *   </dt>
 *   <dt><b>Copyright</b><dd>&copy 2003 Highbar Software Corporation</dd></dt>
 * </dl>
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class UnsynchronizedStack<T>
{
    /**
     * holds the stack elements
     */
    private List<T> list;

    /**
     * Creates a new stack with no elements
     */
    public UnsynchronizedStack()
    {
        list = new ArrayList<T>();
    }

    public UnsynchronizedStack( UnsynchronizedStack<T> stack )
    {
        list = new ArrayList<T>( stack.getList() );
    }

    /**
     * Places an object on the top of the stack
     * @param object the object to push onto the stack
     */
    public void push( T object )
    {
	    getList().add( object );
    }

    /**
     * Removes the top-most object from the stack and
     * returns it
     * @return the top-most object on the stack
     * @throws EmptyStackException if the stack is empty
     */
    public T pop()
    {
        int	length = getList().size();

        if ( length == 0 )
        {
            throw new EmptyStackException();
        }

        T object = getList().get( length - 1 );

        getList().remove( length - 1 );

        return object;
    }

    /**
     * Returns the top-most object on the stack without
     * modifying the contents of the stack
     * @return the object at the top of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public T peek()
    {
        int	length = getList().size();

        if ( length == 0 )
        {
            throw new EmptyStackException();
        }

        return getList().get( length - 1 );
    }

    public void clear()
    {
        getList().clear();
    }

    /**
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty()
    {
	    return getList().size() == 0;
    }

    /**
     * @return the number of items on the stack
     */
    public int size()
    {
        return getList().size();
    }

    /**
     * @return the underlying list
     */
    private List<T> getList()
    {
        return list;
    }
}