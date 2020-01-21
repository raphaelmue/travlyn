package org.travlyn.server.api;

public class NotFoundException extends ApiException {

    public NotFoundException(int code, String msg) {
        super(code, msg);
    }
}
