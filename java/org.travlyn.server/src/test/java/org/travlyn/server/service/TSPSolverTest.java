package org.travlyn.server.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.travlyn.shared.model.api.Stop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Tag("unit")
public class TSPSolverTest {

    @Test
    public void testMultipleSolve() {
        List<Stop> stops = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("example-tsp.txt")))) {
            while (reader.ready()) {
                String[] line = reader.readLine().split(",");
                stops.add(new Stop()
                        .latitude(Double.parseDouble(line[0]))
                        .longitude(Double.parseDouble(line[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double average = 0;
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 100; i++) {
            Collections.shuffle(stops);
            final Stop stop = stops.get(0);
            List<Stop> result = new TSPSolver(stops).solve();
            final double fitness = TSPSolver.getTripLength(result);

            average += fitness;
            best = Double.min(best, fitness);

            assertThat(result).size().isEqualTo(48);
            assertThat(result).first().isEqualToComparingFieldByField(stop);
        }
        average /= 100;

        System.out.println("Average: " + average);
        System.out.println("Best: " + best);

        assertThat(average).isLessThanOrEqualTo(3.1e8);
        // best result is 3.03657499E8
        assertThat(best).isLessThanOrEqualTo(3.04e8);
    }

}