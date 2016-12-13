package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   An enumeration of basic object properties and selectables. This list may be incomplete
 *   so add to it as necessary
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum Basics implements Selectable
{
    current,
    description,
    exists,
    first,
    from,
    grantee,
    granteeaccess,
    granteesignature,
    grantkey,
    grantor,
    history,
    id,
    islockingenforced,
    last,
    locked,
    locker,
    modified,
    name,
    next,
    originated,
    owner,
    policy,
    previous,
    reserved,
    reservedby,
    reservedcomment,
    reservedstart,
    revision,
    to,
    type,
    vault,
    from_name( "from.name" );

    /**
     * holds the proper name of the Basic property if it is different from the declared enum name
     */
    private String overrideName;

    /**
     * Use this constructor when the enum name exactly matches the Basic property name in Matrix
     */
    private Basics() {}

    /**
     * Use this constructor when the enum name does NOT match the actual Basic property name in Matrix.
     * @param overrideName the actual name of the Basic property in matrix
     */
    private Basics( String overrideName )
    {
        this.overrideName = overrideName;
    }

    /**
     * @return the actual database name of the basic property
     */
    public String s()
    {
        return overrideName != null ? overrideName : name();
    }
}