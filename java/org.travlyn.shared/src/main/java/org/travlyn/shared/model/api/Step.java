package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.travlyn.shared.model.db.DataEntity;

import java.util.List;

public class Step extends AbstractDataTransferObject {

    @JsonProperty("type")
    @ApiModelProperty(value = "Type of instruction", required = true, example = "1")
    private int type;

    @JsonProperty("instruction")
    @ApiModelProperty(value = "Instruction text", required = true, example = "1")
    private String instruction;

    @JsonProperty("waypoints")
    @ApiModelProperty(value = "List of indices of associated waypoints", required = true)
    private List<Integer> waypointIndices;

    public int getType() {
        return type;
    }

    public Step setType(int type) {
        this.type = type;
        return this;
    }

    public String getInstruction() {
        return instruction;
    }

    public Step setInstruction(String instruction) {
        this.instruction = instruction;
        return this;
    }

    public List<Integer> getWaypointIndices() {
        return waypointIndices;
    }

    public Step setWaypointIndices(List<Integer> waypointIndices) {
        this.waypointIndices = waypointIndices;
        return this;
    }

    @Override
    public DataEntity toEntity() {
        //no entity for this DTO necessary
        return null;
    }
}
