package org.travlyn.server.model;

public abstract class AbstractDataTransferObject implements DataTransferObject {
    @Override
    public String toString() {
        return this.toJson();
    }
}
