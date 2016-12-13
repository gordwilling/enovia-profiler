package com.highbar.matrix;

import com.highbar.matrix.schema.*;
import com.highbar.util.Strings;
import matrix.db.Context;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <dl> <dt><b>Description:</b> <dd>
 *   Utility class for running unnamed (temp) queries in Matrix.  Example:
 *       <p>
 *       <tt>
 *         <pre>
 *         TempQueryResults results = new TempQuery()
 *                  .type( "Part" )
 *                  .name( "ABC*" )
 *                  .revision( "1" )
 *                  .vault( "eService Production" )
 *                  .select( "id", "type", "name", "revision" )
 *                  .run( context );
 *
 *         for ( BusinessObjectWithSelect b : results )
 *         {
 *              ...
 *         }
 *         </pre>
 *       </tt>
 * </dd> </dt> <dt><b>Copyright</b><dd>&copy 2007 Gordon Wallace</dd></dt> </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public final class TempQuery 
{
    /**
     * Log4J Logger instance
     */
    private final Logger log = Logger.getLogger( getClass() );

    public static final String DEFAULT_TYPE_PATTERN = "*";
    public static final String DEFAULT_NAME_PATTERN = "*";
    public static final String DEFAULT_REVISION_PATTERN= "*";
    public static final String DEFAULT_VAULT_PATTERN = "*";
    public static final String DEFAULT_OWNER_PATTERN = "*";
    public static final String DEFAULT_SEARCH_FORMAT_PATTERN = "*";
    public static final String DEFAULT_SEARCH_TEXT_PATTERN = "";
    public static final String DEFAULT_WHERE_EXPRESSION = "";
    public static final short DEFAULT_OBJECT_LIMIT = (short)0;
    public static final boolean DEFAULT_EXPAND_TYPE = true;

    private Set<String> typePatterns;
    private Set<String> namePatterns;
    private Set<String> revisionPatterns;
    private Set<String> vaultPatterns;
    private Set<String> ownerPatterns;
    private Set<String> searchFormatPatterns;
    private Set<String> searchTextPatterns;
    private Set<String> selectables = new HashSet<String>();
    private Set<String> orderBySelectables;

    private String whereExpression = DEFAULT_WHERE_EXPRESSION;
    private short objectLimit = DEFAULT_OBJECT_LIMIT;
    private boolean expandType = DEFAULT_EXPAND_TYPE;

    /**
     * Performs the temp query using the previously configured set of query parameters
     * @param context the eMatrix database connection context
     * @return the query results iterator
     * @throws MatrixException if the query fails fro some reason
     */
    @SuppressWarnings( "deprecation" )
    public TempQueryResults run( Context context ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context ); }

        if ( getSelectables().size() == 0 )
        {
            throw new IllegalStateException( "No selectables have been set for the query.  " +
                    "Selectables can be added by calling one of the TempQuery.select() methods" );
        }

        TempQueryResults results;

        Query query = getQuery( context );

        if ( log.isInfoEnabled() ) { log.info( "<mql>" + this ); }

        results = new TempQueryResults( query.select( context, getSelectables() ) );
        query.close(context);

        return results;
    }

    /**
     * Performs the temp query using the previously configured set of
     * query parameters and the specified page size. With the page size specified, the query will start
     * streaming results to the client as soon as the first page of results is retrieved
     * <p/><br/>
     * <b>Note:</b> Due to a design issue in the ADK, the returned iterator from a paged query can only iterate
     * through the results once, and cannot be reset.  If you need to iterate over the results multiple times, you
     * either need to call this method again, or use the other {@link #run(Context)} method
     * @param context the eMatrix database connection context
     * @param pageSize stream the returned results using the specified page size
     * @return a query results iterator
     * @throws MatrixException if the query fails for some reason
     */
    public TempQueryResults run( Context context, short pageSize ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context + "|" + pageSize ); }

        if ( getSelectables().size() == 0 )
        {
            throw new IllegalStateException( "No selectables have been set for the query.  " +
                    "Selectables can be added by calling one of the TempQuery.select() methods" );
        }

        TempQueryResults results;

        Query query = getQuery( context );

        if ( getOrderBySelectables() == null )
        {
            results = new TempQueryResults( query.getIterator( context, getSelectables(), pageSize ) );
        }
        else
        {
            results = new TempQueryResults( query.getIterator( context, getSelectables(), pageSize,
                    getOrderBySelectables() ) );
        }
        query.close(context);

        return results;
    }

    private Query getQuery( Context context ) throws MatrixException
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + context ); }

        Query query = new Query( "" );
        query.open( context );
        query.setBusinessObjectType( getTypePattern() );
        query.setBusinessObjectName( getNamePattern() );
        query.setBusinessObjectRevision( getRevisionPattern() );
        query.setVaultPattern( getVaultPattern() );
        query.setOwnerPattern( getOwnerPattern() );
        query.setExpandType( getExpandType() );
        query.setObjectLimit( getObjectLimit() );
        query.setSearchFormat( getSearchFormatPattern() );
        query.setSearchText( getSearchTextPattern() );
        query.setWhereExpression( getWhereExpression() );

        // In the past this used to be required, but it appears that the framework classes
        // don't call this method anymore
        // query.update( context );

        if ( log.isInfoEnabled() ) { log.info( "<mql>" + this ); }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + query ); }

        return query;
    }

    /**
     * Adds an array of selectables to the set of selectables to retrieve during the query. This method
     * can be called multiple times with an additive effect
     * @param selectables the selectable(s) to retrieve
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery select( Selectable... selectables )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Entry: " + Arrays.toString( selectables ) );
        }

        this.selectables.addAll( Selectables.toList( selectables ) );

        if ( log.isDebugEnabled() )
        {
            log.debug( "Exit: " + this );
        }

        return this;
    }

    /**
     * Adds an array of selectables to the set of selectables to retrieve during the query. This method
     * can be called multiple times with an additive effect
     * @param selectables the selectable(s) to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery select( String... selectables )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( selectables ) ); }

        this.selectables.addAll( Arrays.asList( selectables ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a set of type patterns to include in the search
     *
     * @param types the type pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery type( Types... types )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Entry: " + Arrays.toString( types ) );
        }

        if ( typePatterns == null )
        {
            typePatterns = new HashSet<String>();
        }

        typePatterns.addAll( AdminObjects.toList( types ) );

        if ( log.isDebugEnabled() )
        {
            log.debug( "Exit: " + this );
        }

        return this;
    }

    /**
     * Adds a pattern to the set of type patterns to include in the search
     * @param pattern the type pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery type( String... pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( pattern ) ); }

        if ( typePatterns == null )
        {
            typePatterns = new HashSet<String>();
        }

        typePatterns.addAll( Arrays.asList( pattern ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of name patterns to include in the search
     * @param pattern the name pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery name( String pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + pattern ); }

        if ( namePatterns == null )
        {
            namePatterns = new HashSet<String>();
        }

        namePatterns.add( pattern );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of revision patterns to include in the search
     * @param pattern the revision pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery revision( String pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + pattern ); }

        if ( revisionPatterns == null )
        {
            revisionPatterns = new HashSet<String>();
        }

        revisionPatterns.add( pattern );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of vault patterns to include in the search
     *
     * @param vaults the name of the vault to search
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery vault( Vaults... vaults )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( vaults ) ); }

        if ( vaultPatterns == null )
        {
            vaultPatterns = new HashSet<String>();
        }

        vaultPatterns.addAll( AdminObjects.toList( vaults ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of vault patterns to include in the search
     * @param pattern the vault pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery vault( String... pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( pattern ) ); }

        if ( vaultPatterns == null )
        {
            vaultPatterns = new HashSet<String>();
        }

        vaultPatterns.addAll( Arrays.asList( pattern ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of owner patterns to include in the search
     * @param pattern the owner pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery owner( String pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + pattern ); }

        if ( ownerPatterns == null )
        {
            ownerPatterns = new HashSet<String>();
        }

        ownerPatterns.add( pattern );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of format patterns to include in the search
     * @param pattern the format pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery searchFormat( String pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + pattern ); }

        if ( searchFormatPatterns  == null )
        {
            searchFormatPatterns = new HashSet<String>();
        }

        searchFormatPatterns.add( pattern );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a pattern to the set of text search patterns to include in the search
     * @param pattern the text search pattern to add
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery searchText( String pattern )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + pattern ); }

        if ( searchTextPatterns == null )
        {
            searchTextPatterns = new HashSet<String>();
        }

        searchTextPatterns.add( pattern );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a selectable to the set of orderby selectables
     *
     * @param selectables the selectable to order the results by
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery orderBy( Selectable... selectables )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( selectables ) ); }

        if ( orderBySelectables == null )
        {
            orderBySelectables = new HashSet<String>();
        }

        orderBySelectables.addAll( Selectables.toList( selectables ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Adds a selectable to the set of orderby selectables
     *
     * @param selectable the selectable to order the results by
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery orderBy( String... selectable )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + Arrays.toString( selectable ) ); }

        if ( orderBySelectables == null )
        {
            orderBySelectables = new HashSet<String>();
        }

        orderBySelectables.addAll( Arrays.asList( selectable ) );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

        /**
     * @param whereExpression the where expression to use in the query.  If escaping
     * is necessary within the where expression, the caller must provide it manually
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery where( String whereExpression )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + whereExpression ); }

        this.whereExpression = whereExpression;

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * Sets the where expression using a parameterized string and its corresponding
     * parameters.  If escaping is necessary within the where expression, the caller must
     * perform it manually
     * @param parameterizedWhereExpression the parameterized expression
     * @param parameters the expression parameters
     * @return this TempQuery object, as a convenience to enable method chaining
     * @see MessageFormat#format(String, Object[])
     */
    public TempQuery where( String parameterizedWhereExpression, Object... parameters )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + parameterizedWhereExpression + "|" + Arrays.toString(
                parameters ) ); }

        this.whereExpression = MessageFormat.format( parameterizedWhereExpression, parameters );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * @param expandType specify false to exclude children of the specified
     * business type.  Default is true (include child types)
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery expandType( boolean expandType )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry: " + expandType  ); }

        this.expandType = expandType;

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * @param objectLimit the maximum number of objects to return.
     * Default value is 0 (return all)
     * @return this TempQuery object, as a convenience to enable method chaining
     */
    public TempQuery limit( short objectLimit )
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        this.objectLimit = objectLimit;

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + this ); }

        return this;
    }

    /**
     * @return the previously set type pattern
     */
    private String getTypePattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String typePattern = getPatternFromSet( typePatterns, DEFAULT_TYPE_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + typePattern ); }

        return typePattern;
    }

    /**
     * @return the previously set name pattern
     */
    private String getNamePattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String namePattern = getPatternFromSet( namePatterns, DEFAULT_NAME_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + namePattern ); }

        return namePattern;
    }

    /**
     * @return the previously set revision pattern
     */
    private String getRevisionPattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String revisionPattern = getPatternFromSet( revisionPatterns, DEFAULT_REVISION_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + revisionPattern ); }

        return revisionPattern;
    }

    /**
     * @return the previously set vault pattern
     */
    private String getVaultPattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String vaultPattern = getPatternFromSet( vaultPatterns, DEFAULT_VAULT_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + vaultPattern ); }

        return vaultPattern;
    }

    /**
     * @return the previously set owner pattern
     */
    private String getOwnerPattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String ownerPattern = getPatternFromSet( ownerPatterns, DEFAULT_OWNER_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + ownerPattern ); }

        return ownerPattern;
    }

    /**
     * @return the previously set format pattern
     */
    private String getSearchFormatPattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String searchFormatPattern = getPatternFromSet( searchFormatPatterns,
                DEFAULT_SEARCH_FORMAT_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + searchFormatPattern ); }

        return searchFormatPattern;
    }

    /**
     * @return the previously set text pattern for the search
     */
    private String getSearchTextPattern()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        String searchTextPattern = getPatternFromSet( searchTextPatterns, DEFAULT_SEARCH_TEXT_PATTERN );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + searchTextPattern ); }

        return searchTextPattern;
    }

    /**
     * @return the previously set where expression
     */
    private String getWhereExpression()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + whereExpression ); }

        return whereExpression;
    }

    /**
     * @return the previously set value of the expandtype query parameter
     */
    private boolean getExpandType()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + expandType ); }

        return expandType;
    }

    /**
     * @return the previously set object limit
     */
    private short getObjectLimit()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry/Exit: " + objectLimit ); }

        return objectLimit;
    }

    /**
     * @return the list of selectables that have been set for the query
     */
    @SuppressWarnings( "unchecked" )
    private StringList getSelectables()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        StringList stringSelectables = new StringList( selectables.size() );

        stringSelectables.addAll( selectables );

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + stringSelectables ); }

        return stringSelectables;
    }

    /**
     * @return the list of selectables used to order the results by
     */
    @SuppressWarnings( "unchecked" )
    private StringList getOrderBySelectables()
    {
        if ( log.isDebugEnabled() ) { log.debug( "Entry" ); }

        StringList stringOrderBySelectables = null;

        if ( orderBySelectables != null )
        {
            stringOrderBySelectables = new StringList( orderBySelectables.size() );
            stringOrderBySelectables.addAll( orderBySelectables );
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + stringOrderBySelectables ); }

        return stringOrderBySelectables;
    }

    /**
     * @return an mql representation of the temp query.  Note: the search format and
     * search text are not included in this representation of the query
     */
    public String toString()
    {
//        String type = MatrixUtils.quote( getTypePattern() );
//        String name = MatrixUtils.quote( getNamePattern() );
//        String revision = MatrixUtils.quote( getRevisionPattern() );
//        String vault = MatrixUtils.quote( getVaultPattern() );
//        String owner = MatrixUtils.quote( getOwnerPattern() );
//        String expandType = getExpandType() ? "" : "!";
//        String objectLimit = String.valueOf( getObjectLimit() );
//        String whereExpression = getWhereExpression();
//        String selectables = Strings.valueOf( getSelectables(), " " );
//
//        String tempQuery = "escape temp query bus {0} {1} {2} vault {3} owner {4} " +
//                "{5}expandtype limit {6} where \"{7}\" select {8};";
//
//        tempQuery = MessageFormat.format( tempQuery, type, name, revision, vault, owner, expandType,
//                objectLimit, whereExpression, selectables );
//
//        return tempQuery;
        return "TempQuery.toString() disabled.  Need to reimplement MQL quoting method";
    }

    /**
     * Converts the given <tt>patternSet</tt> to a comma-delimited string.  If
     * <tt>patternSet</tt> is null, the <tt>defaultValue</tt> is returned
     * @param patternSet the set of patterns to join
     * @param defaultValue the value to return if <tt>patternSet</tt> is null
     * @return the query pattern represented by the set, or the default value
     */
    private String getPatternFromSet( Set patternSet, String defaultValue )
    {
        String pattern;

        if ( patternSet != null )
        {
            pattern = Strings.valueOf( patternSet, "," );
        }
        else
        {
            pattern = defaultValue;
        }

        if ( log.isDebugEnabled() ) { log.debug( "Exit: " + pattern ); }

        return pattern;
    }
}