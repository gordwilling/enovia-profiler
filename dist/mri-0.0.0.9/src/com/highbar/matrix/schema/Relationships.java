package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   An enumeration of Matrix Relationship Type names
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum Relationships implements AdminObject
{
    BooleanCompatibilityRule( "Boolean Compatibility Rule" ),
    EBOM,
    lgeProductLineProduct,
    ManufacturingResponsibility ( "Manufacturing Responsibility" ),
    ProductVersion( "Product Version"),
    ;

    /**
     * holds the database name of the Relationship if it is different from the declared enum name
     */
    private String overrideName;

    /**
     * Use this constructor when the enum name exactly matches the Relationship type name in Matrix
     */
    private Relationships() {}

    /**
     * Use this constructor when the enum name does NOT match the actual Relationship type name in Matrix.
     * @param overrideName the actual name of the attribute in matrix
     */
    private Relationships( String overrideName )
    {
        this.overrideName = overrideName;
    }

    /**
     * @return the actual database name of the relationship type
     */
    public String n()
    {
        return overrideName != null ? overrideName : name();
    }
}