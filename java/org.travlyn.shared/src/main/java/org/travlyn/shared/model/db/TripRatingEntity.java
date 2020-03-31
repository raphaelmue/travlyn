package org.travlyn.shared.model.db;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trip_rating")
public class TripRatingEntity extends RatingEntity {

    @ManyToOne(targetEntity = TripEntity.class)
    private TripEntity trip;

    public TripEntity getTrip() {
        return trip;
    }

    public void setTrip(TripEntity stop) {
        this.trip = stop;
    }
}
