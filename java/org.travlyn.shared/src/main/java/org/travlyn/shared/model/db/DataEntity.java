package org.travlyn.shared.model.db;

import java.io.Serializable;

public interface DataEntity extends Serializable {
    /**
     * Returns the ID of the entity
     *
     * @return identifier
     */
    int getId();

}
