package org.travlyn.server.externalapi.access;

public interface Request<K> {

    /**
     * Returns the result of the request.
     *
     * @return object of K
     */
    K getResult() throws Exception;
}
