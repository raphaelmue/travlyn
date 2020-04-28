package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.travlyn.shared.model.db.DataEntity;

import java.util.ArrayList;
import java.util.List;


public class ExecutionInfo extends AbstractDataTransferObject {
    @JsonProperty("trip_id")
    @ApiModelProperty(value = "Trip Id", required = true, example = "123")
    private int tripId = -1;

    @JsonProperty("steps")
    @ApiModelProperty(value = "Direction Steps", required = true)
    private List<Step> steps = new ArrayList<>();

    @JsonProperty("distance")
    @ApiModelProperty(value = "Total Distance in Km", required = true, example = "5.1")
    private double distance = 0.0;

    @JsonProperty("duration")
    @ApiModelProperty(value = "Duration in min", required = true, example = "120.5")
    private double duration = 0.0;

    @JsonProperty("waypoints")
    @ApiModelProperty(value = "Waypoints to show on map", required = true)
    private List<Waypoint> waypoints = new ArrayList<>();

    @JsonProperty("stopIds")
    @ApiModelProperty(value = "Order of stop IDs in which the trip will be executed", required = true)
    private List<Integer> stopIds = new ArrayList<>();

    public int getTripId() {
        return tripId;
    }

    public ExecutionInfo setTripId(int tripId) {
        this.tripId = tripId;
        return this;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public ExecutionInfo setSteps(List<Step> steps) {
        this.steps = steps;
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public ExecutionInfo setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public double getDuration() {
        return duration;
    }

    public ExecutionInfo setDuration(double duration) {
        this.duration = duration;
        return this;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public ExecutionInfo setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
        return this;
    }

    public List<Integer> getStopIds() {
        return stopIds;
    }

    public ExecutionInfo setStopIds(List<Integer> stopIds) {
        this.stopIds = stopIds;
        return this;
    }

    @Override
    public DataEntity toEntity() {
        //No entity for this DTO possible
        return null;
    }
}
