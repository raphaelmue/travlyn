package org.travlyn.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stop
 */
@Validated
public class Stop {
    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("longitude")
    private Double longitude = null;

    @JsonProperty("latitude")
    private Double latitude = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("pricing")
    private Double pricing = null;

    @JsonProperty("time_effort")
    private Double timeEffort = null;

    @JsonProperty("average_rating")
    private Double averageRating = null;

    @JsonProperty("ratings")
    @Valid
    private List<Rating> ratings = null;

    @JsonProperty("category")
    private Category category = null;

    public Stop id(Long id) {
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

    public Stop longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Get longitude
     *
     * @return longitude
     **/
    @ApiModelProperty(value = "")

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Stop latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Get latitude
     *
     * @return latitude
     **/
    @ApiModelProperty(value = "")

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Stop name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(value = "")

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @ApiModelProperty(value = "Additional information about stop")

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    @ApiModelProperty(value = "Average pricing for one person in USD")

    public Double getPricing() {
        return pricing;
    }

    public void setPricing(Double pricing) {
        this.pricing = pricing;
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
    @ApiModelProperty(value = "Time effort in hours")

    public Double getTimeEffort() {
        return timeEffort;
    }

    public void setTimeEffort(Double timeEffort) {
        this.timeEffort = timeEffort;
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
    @ApiModelProperty(value = "Average percentage rating by user")

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
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
    @ApiModelProperty(value = "")
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
     * Get category
     *
     * @return category
     **/
    @ApiModelProperty(value = "")

    @Valid
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Stop {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
        sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    pricing: ").append(toIndentedString(pricing)).append("\n");
        sb.append("    timeEffort: ").append(toIndentedString(timeEffort)).append("\n");
        sb.append("    averageRating: ").append(toIndentedString(averageRating)).append("\n");
        sb.append("    ratings: ").append(toIndentedString(ratings)).append("\n");
        sb.append("    category: ").append(toIndentedString(category)).append("\n");
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
