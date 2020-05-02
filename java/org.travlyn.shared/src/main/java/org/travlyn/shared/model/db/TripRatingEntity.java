package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.DataTransferObject;
import org.travlyn.shared.model.api.Rating;

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

    @Override
    public Rating toDataTransferObject() {
        Rating result = new Rating();
        result.description(this.getDescription())
                .rating(this.getRating())
                .user(this.getUser().toDataTransferObject())
                .id(this.getId());
        return result;
    }
}
