package org.travlyn.server.service;

import org.travlyn.server.db.model.TripEntity;

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
