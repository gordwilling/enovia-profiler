package com.highbar.matrix;

import com.highbar.matrix.schema.Relationships;
import com.highbar.matrix.schema.Selectable;
import com.highbar.matrix.schema.Types;
import com.highbar.util.DefaultTreeNode;
import com.highbar.util.Strings;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.db.RelationshipWithSelect;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <dl>
 *   <dt><b>Description:</b>
 *     <dd>
 *       Use this class for simpler and easier-to-use expand-select functionality
 *       [compared with BusinessObject.expandSelect()].  For example:
 *       <p>
 *       <tt>
 *         <pre>
 *         BusinessObject part = new BusinessObject(...);
 *         ExpandResults results = new Expand( part )
 *                  .from()
 *                  .rel( "EBOM" )
 *                  .type( "Part" )
 *                  .selectRel( "Find Number", "Reference Designator" )
 *                  .selectBus( "id", "type", "name", "revision" )
 *                  .recurseToAll()
 *                  .flatExpand();
 *
 *         for ( RelationshipWithSelect r : results )
 *         {
 *              ...
 *         }
 *         </pre>
 *       </tt>
 *     </dd>
 *   </dt>
 *   <dt><b>Copyright</b><dd>&copy 2003 Gordon Wallace</dd></dt>
 * </dl>
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public final class Expand
{
    /**
     * Log4J Logger instance
     */
    private final Logger log = Logger.getLogger( getClass() );

    /**
     * The object from which the expansion is done to retrieve the selectable
     * data
     */
    private BusinessObject rootObject;

    /**
     * Contains the relationship type patterns that
     * are to be traversed in the expand
     */
    private Set<String> relTypePatterns = new HashSet<String>();

    /**
     * Contains the business type patterns that are
     * to be included in the expand
     */
    private Set<String> busTypePatterns = new HashSet<String>();

    /**
     * Contains the relationship selectables that are
     * to be retrieved
     */
    private Set<String> relSelectables = new HashSet<String>();

    /**
     * Contains the business object selectables that
     * are to be retrieved
     */
    private Set<String> busSelectables = new HashSet<String>();

    /**
     * The business object where clause
     */
    private String busWhereClause = "";

    /**
     * The relationship where clause
     */
    private String relWhereClause = "";

    /**
     * Set to 'true' if the expand should be done from the 'to' side
     * of business objects
     */
    private boolean expandTo = false;

    /**
     * Set to 'true' if the expand should be done from the 'from' side
     * of business objects
     */
    private boolean expandFrom = false;

    /**
     * From ADK Documentation: The number of levels to expand, 0 equals expand all; -1 equals expand
     * to end; -2 equals expand to rel.
     */
    private short expandLevel = (short)0;

    /**
     * Indicates that the selectable data retrieved during the expand
     * should be inserted into Lists by default.  (See eMatrix ADK
     * documentation on BusinessObject.expandSelect() for details)
     */
    private boolean useLists = false;

    /**
     * the maximum number of objects to return
     */
    private short limit = (short)0;

    /**
     * From ADK Documentation: true to check for hidden types per MX_SHOW_HIDDEN_TYPE_OBJECTS setting; false to
     * return all objects, even if hidden
     */
    private boolean checkHidden = false;

    /**
     * (not sure what this does - ADK documentation doesn't mention this parameter!)
     */
    private boolean preventDuplicates = false;

    /**
     * Creates a new Expander instance with the specified
     * BusinessObject at its root.
     * @param rootObject the business object to be at the root of the expansion
     */
    public Expand( BusinessObject rootObject )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + rootObject ); }

        setRootObject( rootObject );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }
    }

    /**
     * Sets a new root object for the next expansion.
     * @param rootObject the new business object to perform the expansion on
     */
    public void setRootObject( BusinessObject rootObject )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + rootObject ); }

        if ( rootObject == null )
        {
            throw new NullPointerException( "parameter: rootObject" );
        }

        this.rootObject = rootObject;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }
    }

    /**
     * Gets the BusinessObject from which the expansion is performed
     * @return the business object at the root of the expansion
     */
    public BusinessObject getRootObject()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + rootObject ); }

        return rootObject;
    }

    /**
     * Adds the specified business type name patterns to that which
     * will be used during the 'expand' operation. Multiple calls to this
     * method will result in multiple patterns being applied during the
     * expand. If no business type pattern is specified, all business
     * types will be included in the expand.
     * @param busTypePattern a business type name pattern to match
     *        during the expand.
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand type( String... busTypePattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( busTypePattern ) ); }

        if ( busTypePattern == null )
        {
            throw new NullPointerException( "parameter: busTypePattern" );
        }

        busTypePatterns.addAll( Arrays.asList( busTypePattern ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified business type name patterns to that which
     * will be used during the 'expand' operation. Multiple calls to this
     * method will result in multiple patterns being applied during the
     * expand. If no business type pattern is specified, all business
     * types will be included in the expand.
     * @param busType a business type name pattern to match
     *        during the expand.
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand type( Types... busType )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( busType ) ); }

        if ( busType == null )
        {
            throw new NullPointerException( "parameter: busType" );
        }

        for ( Types t : busType )
        {
            busTypePatterns.add( t.n() );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified relationship type name patterns to that which
     * will be used during the 'expand' operation. Multiple calls to this
     * method will result in multiple patterns being applied during the
     * expand. If no relationship pattern is specified, all relationship
     * types will be included in the expand.
     * @param relTypePattern relationship type name patterns to match
     *        during the expand.
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand rel( String... relTypePattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( relTypePattern ) ); }

        if ( relTypePattern == null )
        {
            throw new NullPointerException( "parameter: relTypePattern" );
        }

        relTypePatterns.addAll( Arrays.asList( relTypePattern ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified relationship type name patterns to that which
     * will be used during the 'expand' operation. Multiple calls to this
     * method will result in multiple patterns being applied during the
     * expand. If no relationship pattern is specified, all relationship
     * types will be included in the expand.
     * @param relType relationship types to match during the expand.
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand rel( Relationships... relType )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( relType ) ); }

        if ( relType == null )
        {
            throw new NullPointerException( "parameter: relType" );
        }

        for ( Relationships r : relType )
        {
            relTypePatterns.add( r.n() );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified selectable(s) to the list of business object selectables
     * whose values are to be retrieved during the expand.
     * @param selectable the name of the selectable to retrieve
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand selectBus( String... selectable )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " +  Arrays.toString( selectable ) ); }

        if ( selectable == null )
        {
            throw new NullPointerException( "parameter: selectable" );
        }

        if ( log.isInfoEnabled() ) { log.info( "adding selectables " + Arrays.toString( selectable ) ); }

        busSelectables.addAll( Arrays.asList( selectable ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified selectable(s) to the list of business object selectables
     * whose values are to be retrieved during the expand.
     * @param selectable the selectable to retrieve
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand selectBus( Selectable... selectable )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " +  Arrays.toString( selectable ) ); }

        if ( selectable == null )
        {
            throw new NullPointerException( "parameter: selectable" );
        }

        if ( log.isInfoEnabled() ) { log.info( "adding selectables " + Arrays.toString( selectable ) ); }

        for ( Selectable s : selectable )
        {
            busSelectables.add( s.s() );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds a collection of selectables to the set of business object selectables whose
     * values are to be retrieved during the expand
     * @param selectables the collection of selectables to retrieve
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand selectBus( Collection<String> selectables )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + selectables ); }

        busSelectables.addAll( selectables );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified selectable(s) to the list of relationship selectables
     * whose values are to be retrieved during the expand.
     * @param selectable the name of the selectable to retrieve
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand selectRel( String... selectable )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( selectable ) ); }

        if ( selectable == null )
        {
            throw new NullPointerException( "parameter: selectable" );
        }

        if ( log.isInfoEnabled() ) { log.info( "adding selectables " + Arrays.toString( selectable ) ); }

        relSelectables.addAll( Arrays.asList( selectable ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds the specified selectable(s) to the list of relationship selectables
     * whose values are to be retrieved during the expand.
     * @param selectable the selectable to retrieve
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand selectRel( Selectable... selectable )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( selectable ) ); }

        if ( selectable == null )
        {
            throw new NullPointerException( "parameter: selectable" );
        }

        if ( log.isInfoEnabled() ) { log.info( "adding selectables " + Arrays.toString( selectable ) ); }

        for ( Selectable s : selectable )
        {
            relSelectables.add( s.s() );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds a collection of selectables to the set of relationship selectables whose
     * values are to be retrieved during the expand
     * @param selectables the collection of selectables to retrieve
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand selectRel( Collection<String> selectables )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + selectables ); }

        relSelectables.addAll( selectables );

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds a where clause to limit the business object data
     * that will be returned in the expand.  By default, no
     * business object where clause is applied
     * @param busWhereClause the MQL "where" expression
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand busWhere( String busWhereClause )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + busWhereClause ); }

        if ( busWhereClause == null )
        {
            throw new NullPointerException( "parameter: busWhereClause" );
        }

        this.busWhereClause = busWhereClause;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Adds a where clause to limit the relationship data
     * that will be returned in the expand.  By default, no relationship
     * where clause is applied.
     * @param relWhereClause the MQL "where" expression
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand relWhere( String relWhereClause )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + relWhereClause ); }

        if ( relWhereClause == null )
        {
            throw new NullPointerException( "parameter: relWhereClause" );
        }

        this.relWhereClause = relWhereClause;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Indicates that the expand should follow the "from" side of the relationship
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand from()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandFrom ); }

        this.expandFrom = true;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Indicates the expand should follow the "to" side of the relationship
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand to()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandTo ); }

        this.expandTo = true;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets the depth of the pending expansion
     * @param expandLevel the depth of the pending expansion. The default is
     *        zero, meaning 'recurse to all'
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand recurseTo( short expandLevel )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandLevel ); }

        this.expandLevel = expandLevel;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets the expand level to 'all'
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand recurseToAll()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandLevel ); }

        this.expandLevel = (short)0;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets the expand level to recurse to end
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand recurseToEnd()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandLevel ); }

        this.expandLevel = (short)-1;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets the expand level to recurse to rel
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand recurseToRel()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandLevel ); }

        this.expandLevel = (short)-2;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets a limit on the number of objects to return. The default value is 0.
     * @param limit the maximum number of objects to include in the results
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand limit( short limit )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + limit ); }

        this.limit = limit;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets the checkHidden flag.  The default value is <tt>false</tt>.
     * @param checkHidden From ADK Documentation: true to check for hidden types per MX_SHOW_HIDDEN_TYPE_OBJECTS setting; false to
     *                    return all objects, even if hidden.
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand checkHidden( boolean checkHidden )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + checkHidden ); }

        this.checkHidden = checkHidden;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Sets the <tt>preventDuplicates</tt> flag.  The default value is <tt>false</tt>.  NOTE: The meaning of this parameter is
     * not explained in the ADK documentation, so this documentation is based on an assumption!
     * @param preventDuplicates true to prevent duplicate objects in the results, false otherwise.
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand preventDuplicates( boolean preventDuplicates )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + preventDuplicates ); }

        this.preventDuplicates = preventDuplicates;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Indicates that the selectable data retrieved during the expand
     * should be inserted into Lists by default.  (See eMatrix ADK
     * documentation on BusinessObject.expandSelect() for details)
     * @param useLists <tt>true</tt> to use Lists for storing
     *        the selectable data. The default value is <tt>false</tt>
     * @return this instance as a convenience to enable builder-style call chaining
     */
    public Expand useLists( boolean useLists )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + useLists ); }

        this.useLists = useLists;

        if ( log.isDebugEnabled() ) { log.debug( "Exit" ); }

        return this;
    }

    /**
     * Performs the <tt>BusinessObject.expandSelect()</tt> operation
     * according to previously set parameters. This method returns a tree data structure that reflects
     * the structure of the data in matrix.  Note that you can only expand in one direction using this
     * method. If you need to expand in both directions, use {@link #flatExpand( Context )} instead
     * @param context the eMatrix database connection context
     * @return the root of the object tree returned by the expand
     * @throws IllegalStateException if the Expander was set to expand in both directions
     * @throws MatrixException in the event of an unspecified error
     */
    public DefaultTreeNode<ExpandNode> treeExpand( Context context ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context ); }

        if ( expandTo && expandFrom )
        {
            throw new IllegalStateException( "Cannot expand into tree when expanding in both directions. " +
                    "If you need to expand in both directions, use flatExpand(Context) instead" );
        }

        ExpandResults expand = flatExpand( context );

        int currentExpandLevel = 0;
        Map<Integer, DefaultTreeNode<ExpandNode>> nodeMap = new HashMap<Integer, DefaultTreeNode<ExpandNode>>();
        BusinessObjectWithSelect rootWithSelect = expand.getRootWithSelect();
        DefaultTreeNode<ExpandNode> rootNode = new DefaultTreeNode<ExpandNode>( new ExpandNode( rootWithSelect ) );
        nodeMap.put( currentExpandLevel, rootNode );

        for ( RelationshipWithSelect r : expand )
        {
            currentExpandLevel = r.getLevel();
            DefaultTreeNode<ExpandNode> parentNode = nodeMap.get( currentExpandLevel - 1 );
            DefaultTreeNode<ExpandNode> thisNode = new DefaultTreeNode<ExpandNode>( parentNode, new ExpandNode( r ) );
            nodeMap.put( currentExpandLevel, thisNode );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + rootNode  ); }

        return rootNode;
    }

    /**
     * Performs the basic expansion operation based on previously set parameters.
     * @param context the eMatrix database connection context
     * @return an expansion results iterator
     * @throws IllegalStateException if an expand direction hasn't been set
     * @throws MatrixException in the event of an unspecified error
     */
    @SuppressWarnings( "deprecation" )
    public ExpandResults flatExpand( Context context ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context ); }

        if ( !(getExpandTo() || getExpandFrom()) )
        {
            throw new IllegalStateException( "An expand direction has not been set" );
        }

        if ( log.isInfoEnabled() )
        {
            log.info( "expanding from object '"
                    + getRootObject().getTypeName() + "' '"
                    + getRootObject().getName() + "' '"
                    + getRootObject().getRevision() + "'" );

            log.info( "expand parameters: " );
            log.info( "  rel pattern: " + getRelPattern() );
            log.info( "  type pattern: " + getTypePattern() );
            log.info( "  bus selectables: " + getBusSelectableStringList() );
            log.info( "  rel selectables: " + getRelSelectableStringList() );
            log.info( "  expand from? " + getExpandFrom() );
            log.info( "  expand to? " + getExpandTo() );
            log.info( "  expand level: " + getExpandLevel() );
            log.info( "  bus where clause: " + getBusWhereClause() );
            log.info( "  rel where clause: " + getRelWhereClause() );
            log.info( "  limit: " + getLimit() );
            log.info( "  checkHidden?: " + getCheckHidden() );
            log.info( "  preventDuplicates?: " + getPreventDuplicates() );
            log.info( "  useLists?: " + getUseLists() );
        }

        ExpandResults expandResults = new ExpandResults( getRootObject().expandSelect( context,
                getRelPattern(),
                getTypePattern(),
                getBusSelectableStringList(),
                getRelSelectableStringList(),
                getExpandTo(),
                getExpandFrom(),
                getExpandLevel(),
                getBusWhereClause(),
                getRelWhereClause(),
                getLimit(),
                getCheckHidden(),
                getUseLists() ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + expandResults ); }

        return expandResults;
    }

    /**
     * Performs the basic expansion operation based on previously set parameters. With the page size specified, the
     * expand operation will start streaming results to the client as soon as the first page of results is retrieved.
     * <p/><br/>
     * <b>Note:</b> Due to a design issue in the ADK, the returned iterator from a paged expand can only iterate
     * through the results once, and cannot be reset.  If you need to iterate over the results multiple times, you
     * either need to call this method again, or use the other expand methods provided
     * @param context the eMatrix database connection context
     * @param pageSize stream the returned results using the specified page size
     * @return an ExpansionResults iterator
     * @throws MatrixException in the event of an unspecified error
     */
    public ExpandResults flatExpand( Context context, short pageSize ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context ); }

        if ( !(getExpandTo() || getExpandFrom()) )
        {
            throw new IllegalStateException( "An expand direction has not been set" );
        }

        if ( log.isInfoEnabled() )
        {
            log.info( "expanding from object '"
                    + getRootObject().getTypeName() + "' '"
                    + getRootObject().getName() + "' '"
                    + getRootObject().getRevision() + "'" );

            log.info( "expand parameters: " );
            log.info( "  rel pattern: " + getRelPattern() );
            log.info( "  type pattern: " + getTypePattern() );
            log.info( "  bus selectables: " + getBusSelectableStringList() );
            log.info( "  rel selectables: " + getRelSelectableStringList() );
            log.info( "  expand from? " + getExpandFrom() );
            log.info( "  expand to? " + getExpandTo() );
            log.info( "  expand level: " + getExpandLevel() );
            log.info( "  bus where clause: " + getBusWhereClause() );
            log.info( "  rel where clause: " + getRelWhereClause() );
            log.info( "  limit: " + getLimit() );
            log.info( "  checkHidden?: " + getCheckHidden() );
            log.info( "  preventDuplicates?: " + getPreventDuplicates() );
            log.info( "  useLists?: " + getUseLists() );
        }

        ExpandResults expandResults = new ExpandResults( getRootObject().getExpansionIterator( context,
                getRelPattern(),
                getTypePattern(),
                getBusSelectableStringList(),
                getRelSelectableStringList(),
                getExpandTo(),
                getExpandFrom(),
                getExpandLevel(),
                getBusWhereClause(),
                getRelWhereClause(),
                getLimit(),
                getCheckHidden(),
                getPreventDuplicates(),
                pageSize,
                getUseLists() ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + expandResults ); }

        return expandResults;
    }

    /**
     * @return the pattern of business object type names to be
     *         examined during the expand
     */
    private String getTypePattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String typePattern = "*";

        if ( busTypePatterns.size() != 0 )
        {
            typePattern = Strings.valueOf( busTypePatterns, "," );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + typePattern ); }

        return typePattern;
    }

    /**
     * @return the pattern of relationship type names to be
     *         examined during the expand
     */
    private String getRelPattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String relPattern = "*";

        if ( relTypePatterns.size() != 0 )
        {
            relPattern = Strings.valueOf( relTypePatterns, "," );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + relPattern ); }

        return relPattern;
    }

    /**
     * @return the StringList of business object selectables to be
     *         retrieved during the expand
     */
    @SuppressWarnings( "unchecked" )
    private StringList getBusSelectableStringList()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        StringList busSelectableList = new StringList( busSelectables.size() );
        busSelectableList.addAll( busSelectables );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + busSelectableList ); }

        return busSelectableList;
    }

    /**
     * @return the StringList of relationship selectables to be retrieved
     *         during the expand
     */
    @SuppressWarnings( "unchecked" )
    private StringList getRelSelectableStringList()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        StringList relSelectableList = new StringList( relSelectables.size() );
        relSelectableList.addAll( relSelectables );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + relSelectableList ); }

        return relSelectableList;
    }

    /**
     * @return the relationship where clause for the expand
     */
    private String getRelWhereClause()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + relWhereClause ); }

        return relWhereClause;
    }

    /**
     * @return the business object where clause for the expand
     */
    private String getBusWhereClause()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + busWhereClause ); }

        return busWhereClause;
    }

    /**
     * @return the value of the <tt>expandFrom</tt> member
     */
    private boolean getExpandFrom()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + expandFrom ); }

        return expandFrom;
    }

    /**
     * @return the value of the <tt>expandTo</tt> member
     */
    private boolean getExpandTo()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + expandTo ); }

        return expandTo;
    }

    /**
     * @return the requested depth of the pending expansion
     */
    private short getExpandLevel()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + expandLevel ); }

        return expandLevel;
    }

    /**
     * @return the value of the <tt>useLists</tt> member
     */
    private boolean getUseLists()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + useLists ); }

        return useLists;
    }

    /**
     * @return the number of objects to return
     */
    private short getLimit()
    {
        return limit;
    }

    /**
     * @return the value of the <tt>checkHidden</tt> flag
     */
    private boolean getCheckHidden()
    {
        return checkHidden;
    }

    /**
     * @return the value of the <tt>preventDuplicates</tt> flag
     */
    private boolean getPreventDuplicates()
    {
        return preventDuplicates;
    }
}