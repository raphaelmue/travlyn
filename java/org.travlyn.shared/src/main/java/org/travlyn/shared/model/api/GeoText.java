package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.GeoTextEntity;

import javax.validation.Valid;
import java.util.Objects;

/**
 * GeoText
 */
@Validated
public class GeoText extends AbstractDataTransferObject {
    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("stop")
    @ApiModelProperty(value = "Stop to which the text refers", required = true)
    private Stop stop = null;

    @JsonProperty("text")
    @ApiModelProperty(value = "Text to be displayed", required = true, example = "123")
    private String text = null;

    public GeoText id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GeoText stop(Stop stop) {
        this.stop = stop;
        return this;
    }

    @Valid
    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public GeoText text(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoText geoText = (GeoText) o;
        return Objects.equals(this.id, geoText.id) &&
                Objects.equals(this.stop, geoText.stop) &&
                Objects.equals(this.text, geoText.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stop, text);
    }

    @Override
    public GeoTextEntity toEntity() {
        GeoTextEntity geoTextEntity = new GeoTextEntity();
        geoTextEntity.setId(this.id);
        geoTextEntity.setStop(this.stop.toEntity());
        geoTextEntity.setText(this.text);
        return geoTextEntity;
    }
}
