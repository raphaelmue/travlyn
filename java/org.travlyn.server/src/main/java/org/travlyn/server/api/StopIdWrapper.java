package org.travlyn.server.api;

import java.util.List;

public class StopIdWrapper {
    private List<Long> stops;

    public List<Long> getStops() {
        return stops;
    }

    public void setStops(List<Long> stops) {
        this.stops = stops;
    }
}
