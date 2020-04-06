package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Rating
 */
@Validated
public class Rating extends AbstractDataTransferObject {
    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("user")
    @ApiModelProperty(value = "User that has created the Rating", required = true)
    private User user = null;

    @JsonProperty("rating")
    @ApiModelProperty(value = "Percentage rating", required = true, example = "0.75")
    private double rating = -1;

    @JsonProperty("description")
    @ApiModelProperty(value = "Rating description", required = true, example = "This is a description of a Rating.")
    private String description = null;

    public Rating id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rating user(User user) {
        this.user = user;
        return this;
    }

    @Valid
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rating rating(double rating) {
        this.rating = rating;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Rating description(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rating rating = (Rating) o;
        return Objects.equals(this.id, rating.id) &&
                Objects.equals(this.user, rating.user) &&
                Objects.equals(this.rating, rating.rating) &&
                Objects.equals(this.description, rating.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, rating, description);
    }

    @Override
    public RatingEntity toEntity() {
        throw new UnsupportedOperationException("A RatingEntity can not be created as it is abstract.");
    }

    public StopRatingEntity toStopEntity(StopEntity stop) {
        StopRatingEntity ratingEntity = new StopRatingEntity();
        ratingEntity.setId(this.id);
        ratingEntity.setStop(stop);
        ratingEntity.setUser(this.user.toEntity());
        ratingEntity.setRating(this.rating);
        ratingEntity.setDescription(this.description);
        return ratingEntity;
    }

    public TripRatingEntity toTripEntity(TripEntity trip) {
        TripRatingEntity ratingEntity = new TripRatingEntity();
        ratingEntity.setId(this.id);
        ratingEntity.setTrip(trip);
        ratingEntity.setUser(this.user.toEntity());
        ratingEntity.setRating(this.rating);
        ratingEntity.setDescription(this.description);
        return ratingEntity;
    }
}
