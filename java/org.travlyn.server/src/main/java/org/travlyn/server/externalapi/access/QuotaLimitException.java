package org.travlyn.server.externalapi.access;

public class QuotaLimitException extends Exception {
    public QuotaLimitException(String msg) {
        super(msg);
    }
}
