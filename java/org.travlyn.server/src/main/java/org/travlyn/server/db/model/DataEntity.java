package org.travlyn.server.db.model;

import java.io.Serializable;

public interface DataEntity extends Serializable {
    /**
     * Returns the ID of the entity
     *
     * @return identifier
     */
    int getId();

}
