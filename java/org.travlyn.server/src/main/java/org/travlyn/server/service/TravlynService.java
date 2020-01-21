package org.travlyn.server.service;

public class TravlynService {

    private static TravlynService instance;

    public static TravlynService getInstance() {
        if (instance == null) {
            instance = new TravlynService();
        }
        return instance;
    }

    private TravlynService() {
    }

}
