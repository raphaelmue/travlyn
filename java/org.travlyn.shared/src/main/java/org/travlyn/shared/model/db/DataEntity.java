package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.DataTransferObject;

import java.io.Serializable;

/**
 * This class represents every entity that is part of the Object Relational Mapping Model.
 */
public interface DataEntity extends Serializable {
    /**
     * Returns the ID of the entity
     *
     * @return identifier
     */
    int getId();

    /**
     * Parses the entity to a data transfer object.
     *
     * @return Data Transfer Object
     */
    default DataTransferObject toDataTransferObject() {
        return null;
    }
}
