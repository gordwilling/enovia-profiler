package com.highbar.matrix;

import matrix.db.BusinessObjectWithSelect;
import matrix.db.RelationshipWithSelect;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class represents a node in an business object expand.  When
 *   the node is the root of the expand, only its business object field will be
 *   populated and the relationship field will be null.  All other nodes will have
 *   both fields populated
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace
 * @version 1.0
 */
public class ExpandNode
{
    private BusinessObjectWithSelect businessObject;
    private RelationshipWithSelect relationship;

    ExpandNode( RelationshipWithSelect relationship )
    {
        this.relationship = relationship;
        this.businessObject = relationship.getTarget();
    }

    ExpandNode( BusinessObjectWithSelect businessObject )
    {
        this.businessObject = businessObject;
    }

    public BusinessObjectWithSelect getBusinessObject()
    {
        return businessObject;
    }

    public RelationshipWithSelect getRelationship()
    {
        return relationship;
    }
}
