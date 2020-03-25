package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
@Validated
public class StopIdWrapper {
    @JsonProperty("stopIds")
    @ApiModelProperty(value = "List of stop ids", required = true)
    @Valid
    private List<Long> stopIds = null;

    public List<Long> getStopIds() {
        return stopIds;
    }

    public void setStopIds(List<Long> stopIds) {
        this.stopIds = stopIds;
    }
}
