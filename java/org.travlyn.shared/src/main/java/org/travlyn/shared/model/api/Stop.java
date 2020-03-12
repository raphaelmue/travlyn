package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.StopEntity;
import org.travlyn.shared.model.db.StopRatingEntity;

import javax.validation.Valid;
import java.util.*;

/**
 * Stop
 */
@Validated
public class Stop extends AbstractDataTransferObject {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("longitude")
    @ApiModelProperty(value = "Longitude", required = true, example = "123.456")
    private double longitude;

    @JsonProperty("latitude")
    @ApiModelProperty(value = "Latitude", required = true, example = "123.456")
    private double latitude;

    @JsonProperty("name")
    @ApiModelProperty(value = "Name", required = true, example = "Statue of Liberty")
    private String name = null;

    @JsonProperty("description")
    @ApiModelProperty(value = "Additional information about stop", required = true, example = "This is a description about the Statue of Liberty")
    private String description = null;

    @JsonProperty("image")
    @ApiModelProperty(value = "Thumbnail for Stop", required = true, example = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Karlsruhe_town_centre_air.jpg/300px-Karlsruhe_town_centre_air.jpg")
    private String image = null;

    @JsonProperty("pricing")
    @ApiModelProperty(value = "Approximate price estimation for one person in USD", required = true, example = "50")
    private Double pricing = null;

    @JsonProperty("timeEffort")
    @ApiModelProperty(value = "Approximate time estimation", required = true, example = "2")
    private Double timeEffort = null;

    @JsonProperty("averageRating")
    @ApiModelProperty(value = "Average percentage rating by user", required = true, example = "0.98")
    private Double averageRating = 0.0;

    @JsonProperty("ratings")
    @ApiModelProperty(value = "List of Ratings by Users")
    @Valid
    private List<Rating> ratings = new ArrayList<>();

    @JsonProperty("category")
    @ApiModelProperty(value = "Category", required = true)
    private Category category = new Category();

    @JsonProperty("city")
    @ApiModelProperty(value = "City the stop belongs to", required = true)
    private City city = null;

    public Stop id(int id) {
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

    public Stop longitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Get Longitude
     *
     * @return longitude
     **/
    public double getLongitude() {
        return longitude;
    }

    public Stop setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Stop latitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Get Latitude
     *
     * @return latitude
     **/
    public double getLatitude() {
        return latitude;
    }

    public Stop setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Stop name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get Name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public Stop setName(String name) {
        this.name = name;
        return this;
    }

    public Stop description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Additional information about stop
     *
     * @return description
     **/
    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Stop image(String image) {
        this.image = image;
        return this;
    }

    public Stop pricing(Double pricing) {
        this.pricing = pricing;
        return this;
    }

    /**
     * Average pricing for one person in USD
     *
     * @return pricing
     **/
    public Double getPricing() {
        return pricing;
    }

    public Stop setPricing(Double pricing) {
        this.pricing = pricing;
        return this;
    }

    public Stop timeEffort(Double timeEffort) {
        this.timeEffort = timeEffort;
        return this;
    }

    /**
     * Time effort in hours
     *
     * @return timeEffort
     **/
    public Double getTimeEffort() {
        return timeEffort;
    }

    public Stop setTimeEffort(Double timeEffort) {
        this.timeEffort = timeEffort;
        return this;
    }

    public Stop averageRating(Double averageRating) {
        this.averageRating = averageRating;
        return this;
    }

    /**
     * Average percentage rating by user
     *
     * @return averageRating
     **/
    public Double getAverageRating() {
        return averageRating;
    }

    public Stop setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
        return this;
    }

    public Stop ratings(List<Rating> ratings) {
        this.ratings = ratings;
        return this;
    }

    public Stop addRatingsItem(Rating ratingsItem) {
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
    @Valid
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Stop category(Category category) {
        this.category = category;
        return this;
    }

    /**
     * Get Category
     *
     * @return category
     **/
    @Valid
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public City getCity() {
        return city;
    }

    public void city(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stop stop = (Stop) o;
        return Objects.equals(this.id, stop.id) &&
                Objects.equals(this.longitude, stop.longitude) &&
                Objects.equals(this.latitude, stop.latitude) &&
                Objects.equals(this.name, stop.name) &&
                Objects.equals(this.description, stop.description) &&
                Objects.equals(this.pricing, stop.pricing) &&
                Objects.equals(this.timeEffort, stop.timeEffort) &&
                Objects.equals(this.averageRating, stop.averageRating) &&
                Objects.equals(this.ratings, stop.ratings) &&
                Objects.equals(this.category, stop.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, longitude, latitude, name, description, pricing, timeEffort, averageRating, ratings, category);
    }

    @Override
    public StopEntity toEntity() {
        StopEntity stopEntity = new StopEntity();
        stopEntity.setId(this.id);
        stopEntity.setLongitude(this.longitude);
        stopEntity.setLatitude(this.latitude);
        stopEntity.setName(this.name);
        stopEntity.setDescription(this.description);
        stopEntity.setPricing(this.pricing);
        stopEntity.setTimeEffort(this.timeEffort);
        stopEntity.setAverageRating(this.averageRating);
        Set<StopRatingEntity> ratingEntities = new HashSet<>();
        this.ratings.forEach(rating -> {
            StopRatingEntity stopRatingEntity = (StopRatingEntity) rating.toEntity();
            stopRatingEntity.setStop(stopEntity);
            ratingEntities.add(stopRatingEntity);
        });
        stopEntity.setRatings(ratingEntities);
        stopEntity.setCategory(this.category.toEntity());
        stopEntity.setCity(this.city.toEntity());
        stopEntity.setImage(this.image);
        return stopEntity;
    }
}
