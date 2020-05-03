package org.travlyn.server.service;

import org.travlyn.shared.model.api.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TSPSolver {
    private static final double TEMPERATURE = 10000;
    private static final double COOLING_FACTOR = 0.999;

    private final Random random = new Random();

    private List<Stop> stops;
    private List<Stop> best;

    private final Stop start;

    public TSPSolver(List<Stop> stops) {
        this.stops = stops;
        this.best = new ArrayList<>(stops);
        this.start = stops.get(0);
    }

    public List<Stop> solve() {
        for (double t = TEMPERATURE; t > 1; t *= COOLING_FACTOR) {
            List<Stop> neighbor = new ArrayList<>(stops);
            Collections.copy(stops, neighbor);

            int index1 = random.nextInt(stops.size());
            int index2 = random.nextInt(stops.size());

            Collections.swap(neighbor, index1, index2);

            int currentLength = getTripLength(stops);
            int neighborLength = getTripLength(neighbor);

            if (Math.random() < probability(currentLength, neighborLength, t)) {
                this.stops = new ArrayList<>(neighbor);
            }

            if (getTripLength(stops) < getTripLength(best)) {
                this.best = new ArrayList<>(stops);
            }
        }

        rotateToStart();
        return best;
    }

    private void rotateToStart() {
        int startPosition = 0;
        for (int i = 0; i < this.best.size(); i++) {
            if (best.get(i).getLatitude() == start.getLatitude() && best.get(i).getLongitude() == start.getLongitude()) {
                startPosition = i;
                break;
            }
        }
        Collections.rotate(best, -startPosition);
    }

    private double probability(double f1, double f2, double temp) {
        if (f2 < f1) return 1;
        return Math.exp((f1 - f2) / temp);
    }

    public static int getTripLength(List<Stop> stops) {
        int totalDistance = 0;

        for (int i = 0; i < stops.size(); i++) {
            Stop start = stops.get(i);
            Stop end = stops.get(i + 1 < stops.size() ? i + 1 : 0);
            totalDistance += distance(start, end);
        }

        return totalDistance;
    }

    private static double distance(Stop stop1, Stop stop2) {
        return distance(stop1.getLatitude(), stop1.getLongitude(), stop2.getLatitude(), stop2.getLongitude(), 0, 0);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
