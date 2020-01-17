package org.travlyn.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trip
 */
@Validated
public class Trip {
    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("user")
    private User user = null;

    @JsonProperty("city")
    private City city = null;

    @JsonProperty("private")
    private Boolean _private = null;

    @JsonProperty("stops")
    @Valid
    private List<Stop> stops = null;

    @JsonProperty("ratings")
    @Valid
    private List<Rating> ratings = null;

    @JsonProperty("geoText")
    @Valid
    private List<GeoText> geoText = null;

    public Trip id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(value = "")

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trip user(User user) {
        this.user = user;
        return this;
    }

    /**
     * Get user
     *
     * @return user
     **/
    @ApiModelProperty(value = "")

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
     * Get city
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

    public Trip _private(Boolean _private) {
        this._private = _private;
        return this;
    }

    /**
     * Get _private
     *
     * @return _private
     **/
    @ApiModelProperty(value = "")

    public Boolean isPrivate() {
        return _private;
    }

    public void setPrivate(Boolean _private) {
        this._private = _private;
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
     * Get stops
     *
     * @return stops
     **/
    @ApiModelProperty(value = "")
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
     * Get ratings
     *
     * @return ratings
     **/
    @ApiModelProperty(value = "")
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
     * Get geoText
     *
     * @return geoText
     **/
    @ApiModelProperty(value = "")
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
                Objects.equals(this._private, trip._private) &&
                Objects.equals(this.stops, trip.stops) &&
                Objects.equals(this.ratings, trip.ratings) &&
                Objects.equals(this.geoText, trip.geoText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, city, _private, stops, ratings, geoText);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Trip {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    user: ").append(toIndentedString(user)).append("\n");
        sb.append("    city: ").append(toIndentedString(city)).append("\n");
        sb.append("    _private: ").append(toIndentedString(_private)).append("\n");
        sb.append("    stops: ").append(toIndentedString(stops)).append("\n");
        sb.append("    ratings: ").append(toIndentedString(ratings)).append("\n");
        sb.append("    geoText: ").append(toIndentedString(geoText)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
