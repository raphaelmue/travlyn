package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.CityEntity;

import java.util.Objects;

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

    @JsonProperty("description")
    @ApiModelProperty(value = "Description text", required = true, example = "This is a description text for New York.")
    private String description = null;

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
        cityEntity.setDescription(this.description);
        return cityEntity;
    }
}
