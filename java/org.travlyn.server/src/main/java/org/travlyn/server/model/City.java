package org.travlyn.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.server.db.model.CityEntity;

import java.util.Objects;

/**
 * City
 */
@Validated
public class City extends AbstractDataTransferObject {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier")
    private int id = -1;

    @JsonProperty("name")
    @ApiModelProperty(value = "Name")
    private String name = null;

    @JsonProperty("image")
    @ApiModelProperty(value = "URL to image")
    private String image = null;

    @JsonProperty("description")
    @ApiModelProperty(value = "Description text")
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
        cityEntity.setName(this.name);
        cityEntity.setImage(this.image);
        cityEntity.setDescription(this.description);
        return cityEntity;
    }
}
