package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Stop;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @AttributeOverrides(value = {
            @AttributeOverride(name = "tripId", column = @Column(name = "trip_id", nullable = false)),
            @AttributeOverride(name = "stopId", column = @Column(name = "stop_id", nullable = false))
    })
    private TripStopId tripStopId = new TripStopId();

    @ManyToOne
    @MapsId(value = "tripId")
    private TripEntity trip;

    @ManyToOne
    @MapsId(value = "stopId")
    private StopEntity stop;

    @OneToOne(targetEntity = TripStopEntity.class, cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void setPredecessor(TripStopEntity predecessor) {
        this.predecessor = predecessor;
    }

    public TripStopEntity getPredecessor() {
        return predecessor;
    }

    @Override
    public Stop toDataTransferObject() {
        return this.stop.toDataTransferObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TripStopEntity that = (TripStopEntity) o;

        if (id != that.id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (tripStopId != null ? tripStopId.hashCode() : 0);
        result = 31 * result + (trip != null ? trip.hashCode() : 0);
        result = 31 * result + (stop != null ? stop.hashCode() : 0);
        result = 31 * result + (predecessor != null ? predecessor.hashCode() : 0);
        return result;
    }

    @Embeddable
    public static class TripStopId implements Serializable {
        @NotNull
        public int tripId;
        @NotNull
        public int stopId;

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