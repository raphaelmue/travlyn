package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.GeoTextEntity;
import org.travlyn.shared.model.db.RatingEntity;
import org.travlyn.shared.model.db.TripEntity;
import org.travlyn.shared.model.db.TripStopEntity;

import javax.validation.Valid;
import java.util.*;

/**
 * Trip
 */
@Validated
public class Trip extends AbstractDataTransferObject {
    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier")
    private int id = -1;

    @JsonProperty("user")
    @ApiModelProperty(value = "User that created the Trip")
    private User user = null;

    @JsonProperty("city")
    @ApiModelProperty(value = "City where the Trip takes place")
    private City city = null;

    @JsonProperty("private")
    @ApiModelProperty(value = "Trip is accessible by others if true")
    private Boolean isPrivate = null;

    @JsonProperty("stops")
    @ApiModelProperty(value = "List of Stops")
    @Valid
    private List<Stop> stops = null;

    @JsonProperty("ratings")
    @ApiModelProperty(value = "List of Ratings")
    @Valid
    private List<Rating> ratings = null;

    @JsonProperty("geoText")
    @ApiModelProperty(value = "List of GeoTexts")
    @Valid
    private List<GeoText> geoText = null;

    public Trip id(int id) {
        this.id = id;
        return this;
    }

    /**
     * Get Identifier
     *
     * @return id
     **/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trip user(User user) {
        this.user = user;
        return this;
    }

    /**
     * Get User that has created the Trip
     *
     * @return user
     **/
    @Valid
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trip city(City city) {
        this.city = city;
        return this;
    }

    /**
     * Get City where the Trip takes place
     *
     * @return city
     **/
    @ApiModelProperty(value = "")

    @Valid
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Trip isPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    /**
     * Get private flag. Trip is accessible by others if true
     *
     * @return _private
     **/
    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Trip stops(List<Stop> stops) {
        this.stops = stops;
        return this;
    }

    public Trip addStopsItem(Stop stopsItem) {
        if (this.stops == null) {
            this.stops = new ArrayList<Stop>();
        }
        this.stops.add(stopsItem);
        return this;
    }

    /**
     * Get List of Stops
     *
     * @return stops
     **/
    @Valid
    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public Trip ratings(List<Rating> ratings) {
        this.ratings = ratings;
        return this;
    }

    public Trip addRatingsItem(Rating ratingsItem) {
        if (this.ratings == null) {
            this.ratings = new ArrayList<Rating>();
        }
        this.ratings.add(ratingsItem);
        return this;
    }

    /**
     * Get List of Ratings
     *
     * @return ratings
     **/
    @Valid
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Trip geoText(List<GeoText> geoText) {
        this.geoText = geoText;
        return this;
    }

    public Trip addGeoTextItem(GeoText geoTextItem) {
        if (this.geoText == null) {
            this.geoText = new ArrayList<GeoText>();
        }
        this.geoText.add(geoTextItem);
        return this;
    }

    /**
     * Get List of GeoTexts
     *
     * @return geoText
     **/
    @Valid
    public List<GeoText> getGeoText() {
        return geoText;
    }

    public void setGeoText(List<GeoText> geoText) {
        this.geoText = geoText;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trip trip = (Trip) o;
        return Objects.equals(this.id, trip.id) &&
                Objects.equals(this.user, trip.user) &&
                Objects.equals(this.city, trip.city) &&
                Objects.equals(this.isPrivate, trip.isPrivate) &&
                Objects.equals(this.stops, trip.stops) &&
                Objects.equals(this.ratings, trip.ratings) &&
                Objects.equals(this.geoText, trip.geoText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, city, isPrivate, stops, ratings, geoText);
    }

    @Override
    public TripEntity toEntity() {
        TripEntity tripEntity = new TripEntity();
        tripEntity.setId(this.id);
        tripEntity.setCity(this.city.toEntity());
        tripEntity.setPrivate(this.isPrivate);
        Set<TripStopEntity> tripStopEntities = new HashSet<>();
        for (int i = 0; i < this.stops.size(); i++) {
            TripStopEntity tripStopEntity = new TripStopEntity();
            tripStopEntity.setIndex(i);
            tripStopEntity.setStop(this.stops.get(i).toEntity());
            tripStopEntities.add(tripStopEntity);
        }
        tripEntity.setStops(tripStopEntities);
        List<RatingEntity> ratingEntities = new ArrayList<>();
        this.ratings.forEach(rating -> ratingEntities.add(rating.toEntity()));
        tripEntity.setRatings(ratingEntities);
        List<GeoTextEntity> geoTextEntities = new ArrayList<>();
        this.geoText.forEach(geoText -> geoTextEntities.add(geoText.toEntity()));
        tripEntity.setGeoTexts(geoTextEntities);
        return tripEntity;
    }
}
