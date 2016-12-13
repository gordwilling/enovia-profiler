package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   An enumeration of Matrix Attribute names.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum Attributes implements AdminObject, Selectable
{
    eServiceConstructorArguments( "eService Constructor Arguments" ),
    eServiceMethodName( "eService Method Name" ),
    eServiceProgramName( "eService Program Name" ),
    eServiceSequenceNumber( "eService Sequence Number" ),
    eServiceTargetStates( "eService Target States" ),
    eServiceErrorType( "eService Error Type" ),
    eServiceProgramArgument1( "eService Program Argument 1" ),
    eServiceProgramArgument2( "eService Program Argument 2" ),
    eServiceProgramArgument3( "eService Program Argument 3" ),
    eServiceProgramArgument4( "eService Program Argument 4" ),
    eServiceProgramArgument5( "eService Program Argument 5" ),
    eServiceProgramArgument6( "eService Program Argument 6" ),
    eServiceProgramArgument7( "eService Program Argument 7" ),
    eServiceProgramArgument8( "eService Program Argument 8" ),
    eServiceProgramArgument9( "eService Program Argument 9" ),
    eServiceProgramArgument10( "eService Program Argument 10" ),
    eServiceProgramArgument11( "eService Program Argument 11" ),
    eServiceProgramArgument12( "eService Program Argument 12" ),
    eServiceProgramArgument13( "eService Program Argument 13" ),
    eServiceProgramArgument14( "eService Program Argument 14" ),
    eServiceProgramArgument15( "eService Program Argument 15" ),
    Originator;

    /**
     * holds the database name of the Attribute if it is different from the declared enum name
     */
    private String overrideName;

    /**
     * Use this constructor when the enum name exactly matches the Attribute name in Matrix
     */
    private Attributes() {}

    /**
     * Use this constructor when the enum name does NOT match the actual Attribute name in Matrix.
     * @param overrideName the actual name of the attribute in matrix
     */
    private Attributes( String overrideName )
    {
        this.overrideName = overrideName;
    }

    /**
     * @return the actual database name of the attribute
     */
    public String n()
    {
        return overrideName != null ? overrideName : name();
    }

    /**
     * Gets the selectable form of the attribute name. For example, if the attribute name
     * is 'Originator', this method would return 'attribute[Originator]'
     * @return the actual database name of the attribute in selectable form.
     */
    public String s()
    {
        return new StringBuilder( "attribute[" ).append( n() ).append( "]").toString();
    }
}