package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   An enumeration of Matrix Business Type names
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum Types implements AdminObject
{
    eServiceTriggerProgramParameters( "eService Trigger Program Parameters" ),
    HardwareProduct( "Hardware Product"),
    PartMaster( "Part Master" ),
    Plant,
    ProductLine( "Product Line" ),
    ProductVariant( "Product Variant" );

    /**
     * holds the database name of the Type if it is different from the declared enum name
     */
    private String overrideName;

    /**
     * Use this constructor when the enum name exactly matches the type name in Matrix
     */
    private Types() {}

    /**
     * Use this constructor when the enum name does NOT match the actual type name in Matrix.
     * @param overrideName the actual name of the attribute in matrix
     */
    private Types( String overrideName )
    {
        this.overrideName = overrideName;
    }

    /**
     * @return the actual database name of the type
     */
    public String n()
    {
        return overrideName != null ? overrideName : name();
    }
}