package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.CityEntity;
import org.travlyn.shared.model.db.StopEntity;

import javax.validation.Valid;
import java.util.*;

/**
 * City
 */
@Validated
public class City extends AbstractDataTransferObject {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("longitude")
    @ApiModelProperty(value = "Longitude", required = true, example = "123.456")
    private double longitude = -1;

    @JsonProperty("latitude")
    @ApiModelProperty(value = "Latitude", required = true, example = "123.456")
    private double latitude = -1;

    @JsonProperty("name")
    @ApiModelProperty(value = "Name", required = true, example = "New York")
    private String name = null;

    @JsonProperty("image")
    @ApiModelProperty(value = "URL to image", required = true, example = "https://example.org/image")
    private String image = null;

    @JsonProperty("unfetchedStops")
    @ApiModelProperty(value = "Boolean to indicate if there are unfetched stops", required = true, example = "false")
    private boolean unfetchedStops = false;

    @JsonProperty("description")
    @ApiModelProperty(value = "Description text", required = true, example = "This is a description text for New York.")
    private String description = null;

    @JsonProperty("stops")
    @ApiModelProperty(value = "List of Stops in city", required = true)
    @Valid
    private List<Stop> stops = null;

    public City id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public City longitude(double longitude) {
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

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public City latitude(double latitude) {
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public City name(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City image(String image) {
        this.image = image;
        return this;
    }

    public String getImage() {
        return image;
    }

    public boolean isUnfetchedStops() {
        return unfetchedStops;
    }

    public City setUnfetchedStops(boolean unfetchedStops) {
        this.unfetchedStops = unfetchedStops;
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public City description(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public City setStops(List<Stop> stops) {
        if (this.stops == null) {
            this.stops = new ArrayList<>();
        }
        this.stops = stops;
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        City city = (City) o;
        return Objects.equals(this.id, city.id) &&
                Objects.equals(this.name, city.name) &&
                Objects.equals(this.image, city.image) &&
                Objects.equals(this.stops, city.stops) &&
                Objects.equals(this.description, city.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, description);
    }

    @Override
    public CityEntity toEntity() {
        CityEntity cityEntity = new CityEntity();
        cityEntity.setId(this.id);
        cityEntity.setLongitude(this.longitude);
        cityEntity.setLatitude(this.latitude);
        cityEntity.setName(this.name);
        cityEntity.setImage(this.image);
        cityEntity.setUnfetchedStops(this.unfetchedStops);
        cityEntity.setDescription(this.description);
        if (stops != null) {
            HashSet<StopEntity> stopEntityHashSet = new HashSet<>();
            for (Stop stop : stops) {
                stopEntityHashSet.add(stop.toEntity());
            }
            cityEntity.setStops(stopEntityHashSet);
        } else {
            cityEntity.setStops(new HashSet<>());
        }
        return cityEntity;
    }
}
