package com.highbar.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <dl>
 *   <dt><b>Description:</b>
 *     <dd>
 *       This class provides a default implementation of the TreeNode
 *       interface
 *     </dd>
 *   </dt>
 *   <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
 * </dl>
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public abstract class AbstractTreeNode<T> implements TreeNode<T>
{
    /**
     * @return true if the node is the root of the tree
     */
    public boolean isRoot()
    {
        return getParent() == null;
    }

    /**
     * @return true if the node is a leaf in the tree (i.e. it has
     *         no children)
     */
    public boolean isLeaf()
    {
        return !hasChildren();
    }

    /**
     * @return true if the node has children
     */
    public boolean hasChildren()
    {
        return getChildren().size() != 0;
    }

    /**
     * @return a preorder Iterator.  This method is provided as a convenience
     *         so that the foreach loop construct can be used to process tree nodes
     * @see #depthFirstIterator()
     */
    public Iterator<TreeNode<T>> iterator()
    {
        return preorderIterator();
    }

    /**
     * @return an Iterator that will traverse the tree in depth-first
     *         order
     */
    public Iterator<TreeNode<T>> depthFirstIterator()
    {
        return new DepthFirstIterator( this );
    }

    /**
     * @return an Iterator that will traverse the tree in preorder
     */
    public Iterator<TreeNode<T>> preorderIterator()
    {
        return new PreorderIterator( this );
    }

    /**
     * <dl>
     *   <dt><b>Description:</b>
     *     <dd>
     *       This class is used to traverse the tree rooted by this
     *       TreeNode in preorder.  (This *should* be the same way that
     *       the eMatrix RelationshipWithSelectItr traverses an
     *       expansion)
     *     </dd>
     *   </dt>
     *   <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
     * </dl>
     * @author Gordon Wallace - gw@highbar.com
     * @version 1.0
     */
    private final class PreorderIterator implements Iterator<TreeNode<T>>
    {
	    private UnsynchronizedStack<Iterator<TreeNode<T>>> stack = null;

        PreorderIterator( TreeNode<T> rootNode )
        {
            List<TreeNode<T>> list = new ArrayList<TreeNode<T>>( 1 );
            list.add( rootNode );

            stack = new UnsynchronizedStack<Iterator<TreeNode<T>>>();
            stack.push( list.iterator() );
        }

        /**
         * @return true if there are more TreeNode instances to
         *         iterate over
         */
        public boolean hasNext()
        {
            return ( !stack.isEmpty() && stack.peek().hasNext() );
        }

        /**
         * @return the next TreeNode in the tree
         */
        public TreeNode<T> next()
        {
            Iterator<TreeNode<T>> iterator = stack.peek();
            TreeNode<T> node = iterator.next();
            Iterator<TreeNode<T>> children = node.getChildren().iterator();

            if ( !iterator.hasNext() )
            {
                stack.pop();
            }
            if ( children.hasNext() )
            {
                stack.push( children );
            }

            return node;
        }

        /**
         * @throws java.lang.UnsupportedOperationException because the underlying data
         *         structure is immutable
         */
        public void remove()
        {
            throw new UnsupportedOperationException( "underlying tree is immutable" );
        }
	}

    /**
     * <dl>
     *   <dt><b>Description:</b>
     *     <dd>
     *       This class is used to traverse the tree rooted by a
     *       TreeNode in depth-first order
     *     </dd>
     *   </dt>
     *   <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
     * </dl>
     * @author Gordon Wallace - gw@highbar.com
     * @version 1.0
     */
    private final class DepthFirstIterator implements Iterator<TreeNode<T>>
    {
        protected TreeNode<T> rootNode;
        protected Iterator<TreeNode<T>> children;
        protected Iterator<TreeNode<T>> childTree;

        DepthFirstIterator( TreeNode<T> rootNode )
        {
            this.rootNode = rootNode;
            this.children = rootNode.getChildren().iterator();
            this.childTree = Collections.<TreeNode<T>>emptyList().iterator();
        }

        /**
         * @return <tt>true</tt> if there are more elements in
         *         the iteration
         */
        public boolean hasNext()
        {
            return rootNode != null;
        }

        /**
         * @return the next <tt>TreeNode</tt> in the data structure
         */
        public TreeNode<T> next()
        {
            TreeNode<T> nextNode;

            if ( childTree.hasNext() )
            {
                nextNode = childTree.next();
            }
            else if ( children.hasNext() )
            {
                childTree = new DepthFirstIterator( children.next() );

                nextNode = childTree.next();
            }
            else
            {
                nextNode = rootNode;
                rootNode = null;
            }

            return nextNode;
        }

        /**
         * @throws java.lang.UnsupportedOperationException because the underlying data
         *         structure is immutable
         */
        public void remove()
        {
            throw new UnsupportedOperationException( "underlying tree is immutable" );
        }
    }
}