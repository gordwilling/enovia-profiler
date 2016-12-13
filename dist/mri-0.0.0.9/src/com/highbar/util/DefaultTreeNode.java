package com.highbar.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Default implementation of the {@link TreeNode} interface
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 * @see TreeNode
 */
public class DefaultTreeNode<T> extends AbstractTreeNode<T>
{
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;
    private T object;

    /**
     * Creates a new root TreeNode encapsulating the given object
     * @param object the object to associate with the root
     */
    public DefaultTreeNode( T object )
    {
        this.parent = null;
        this.object = object;
    }

    /**
     * Creates a new TreeNode with the specified parent, and associated with the given object
     * @param parent the parent of this node
     * @param object the object to associate with this node
     */
    public DefaultTreeNode( TreeNode<T> parent, T object )
    {
        this.parent = parent;
        this.object = object;

        parent.addChild( this );
    }

    /**
     * @return the object encapsulated by this node
     */
    public T getObject()
    {
        return object;
    }

    /**
     * @return the distance between this node and the root, i.e. the number of parent nodes between the root node and
     * this one
     */
    public int getLevel()
    {
        int level = 0;
        TreeNode n = this;

        while ( n.getParent() != null )
        {
            level++;
            n = n.getParent();
        }
        return level;
    }

    /**
     * @return the parent of this node
     */
    public TreeNode<T> getParent()
    {
        return parent;
    }

    /**
     * @return the immediate (first level) children of this node
     */
    public List<TreeNode<T>> getChildren()
    {
        if ( children == null )
        {
            return Collections.emptyList();
        }
        else
        {
            return new ArrayList<TreeNode<T>>( children );
        }
    }

    /**
     * Adds a child to the set of this nodes children
     * @param child the child node to add
     */
    public void addChild( TreeNode<T> child )
    {
        if ( children == null )
        {
            children = new ArrayList<TreeNode<T>>();
        }
        children.add( child );
    }
}