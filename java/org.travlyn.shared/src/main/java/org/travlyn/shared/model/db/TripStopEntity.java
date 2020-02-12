package org.travlyn.shared.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "trip_stop")
public class TripStopEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Embedded
    private TripStopId tripStopId = new TripStopId();

    @ManyToOne
    @MapsId(value = "tripId")
    private TripEntity trip;

    @ManyToOne
    @MapsId(value = "stopId")
    private StopEntity stop;

    @OneToOne(targetEntity = TripStopEntity.class)
    @JoinColumn(name = "predecessor", referencedColumnName = "id")
    private TripStopEntity predecessor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TripStopId getTripStopId() {
        return tripStopId;
    }

    public void setTripStopId(TripStopId id) {
        this.tripStopId = id;
    }

    public TripEntity getTrip() {
        return trip;
    }

    public void setTrip(TripEntity trip) {
        this.trip = trip;
    }

    public StopEntity getStop() {
        return stop;
    }

    public void setStop(StopEntity stop) {
        this.stop = stop;
    }

    public TripStopEntity getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(TripStopEntity predecessor) {
        this.predecessor = predecessor;
    }

    @Embeddable
    public static class TripStopId implements Serializable {
        private int tripId;
        private int stopId;

        public int getTripId() {
            return tripId;
        }

        public void setTripId(int tripId) {
            this.tripId = tripId;
        }

        public int getStopId() {
            return stopId;
        }

        public void setStopId(int stopId) {
            this.stopId = stopId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TripStopId that = (TripStopId) o;
            return tripId == that.tripId &&
                    stopId == that.stopId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tripId, stopId);
        }
    }
}