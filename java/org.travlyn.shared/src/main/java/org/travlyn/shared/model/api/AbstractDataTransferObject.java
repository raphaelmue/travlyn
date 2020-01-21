package org.travlyn.shared.model.api;

public abstract class AbstractDataTransferObject implements DataTransferObject {
    @Override
    public String toString() {
        return this.toJson();
    }
}
