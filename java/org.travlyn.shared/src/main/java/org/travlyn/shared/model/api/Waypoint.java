package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.travlyn.shared.model.db.DataEntity;

public class Waypoint extends AbstractDataTransferObject{

    @JsonProperty("latitude")
    @ApiModelProperty(value = "Latitude of waypoint", required = true, example = "0.5")
    private double latitude = 0.0;

    @JsonProperty("longitude")
    @ApiModelProperty(value = "Longitude of waypoint", required = true, example = "0.5")
    private double longitude = 0.0;

    public double getLatitude() {
        return latitude;
    }

    public Waypoint setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public Waypoint setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public DataEntity toEntity() {
        //No entity for this DTO necessary
        return null;
    }
}
