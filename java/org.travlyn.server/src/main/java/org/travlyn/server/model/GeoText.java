package org.travlyn.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

/**
 * GeoText
 */
@Validated
public class GeoText {
    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("stop")
    private Stop stop = null;

    @JsonProperty("text")
    private String text = null;

    public GeoText id(Long id) {
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

    public GeoText stop(Stop stop) {
        this.stop = stop;
        return this;
    }

    /**
     * Get stop
     *
     * @return stop
     **/
    @ApiModelProperty(value = "")

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

    /**
     * Get text
     *
     * @return text
     **/
    @ApiModelProperty(value = "")

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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GeoText {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    stop: ").append(toIndentedString(stop)).append("\n");
        sb.append("    text: ").append(toIndentedString(text)).append("\n");
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
