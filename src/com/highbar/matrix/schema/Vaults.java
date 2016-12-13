package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   An enumeration of Matrix Vault names
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum Vaults implements AdminObject
{
    eServiceSample( "eService Sample" ),
    eServiceProduction( "eService Production" ),
    eServiceAdministration( "eService Administration" );

    /**
     * holds the database name of the Vault if it is different from the declared enum name
     */
    private String overrideName;

    /**
     * Use this constructor when the enum name exactly matches the vault name in Matrix
     */
    private Vaults() {}

    /**
     * Use this constructor when the enum name does NOT match the actual vault name in Matrix.
     * @param overrideName the actual name of the attribute in matrix
     */
    private Vaults( String overrideName )
    {
        this.overrideName = overrideName;
    }

    /**
     * @return the actual database name of the vault
     */
    public String n()
    {
        return overrideName != null ? overrideName : name();
    }
}