package com.highbar.util;

import java.util.Iterator;
import java.util.List;

/**
 * <dl>
 *   <dt><b>Description:</b>
 *     <dd>
 *       This interface provides the methods for creating and
 *       traversing a tree data structure
 *     </dd>
 *   </dt>
 *   <dt><b>Copyright</b><dd>&copy 2008 Highbar Software Corporation</dd></dt>
 * </dl>
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public interface TreeNode<T> extends Iterable<TreeNode<T>>
{
    /**
     * @return the object stored in this tree node
     */
    public T getObject();

    /**
     * @return true if the node is the root of the tree
     */
    public boolean isRoot();

    /**
     * @return true if the node is a leaf in the tree (i.e. it has
     *         no children)
     */
    public boolean isLeaf();

    /**
     * @return true if the node has children
     */
    public boolean hasChildren();

    /**
     * @return the parent of this node
     */
    public TreeNode<T> getParent();

    /**
     * @return the Set of this node's children
     */
    public List<TreeNode<T>> getChildren();

    /**
     * @param child the child node to add
     */
    public void addChild( TreeNode<T> child );

    /**
     * @return the depth of the node in the tree.  The root node is at level zero and its
     *         direct children are at level 1, etc.
     */
    public int getLevel();

    /**
     * @return an Iterator that will traverse the tree in an order
     *         specified by the implementation.  This is provied for
     *         to enable use of the foreach loop
     */
    public Iterator<TreeNode<T>> iterator();

    /**
     * @return an Iterator that will traverse the tree in depth-first
     *         order
     */
    public Iterator<TreeNode<T>> depthFirstIterator();

    /**
     * @return an Iterator that will traverse the tree in preorder
     */
    public Iterator<TreeNode<T>> preorderIterator();
}
