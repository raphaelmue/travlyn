package org.travlyn.server.externalapi.access;

public class QuotaLimitException extends RuntimeException {
    public QuotaLimitException(String msg) {
        super(msg);
    }
}
