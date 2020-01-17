package org.travlyn.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Rating
 */
@Validated
public class Rating {
    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("user")
    private User user = null;

    @JsonProperty("rating")
    private Double rating = null;

    @JsonProperty("description")
    private String description = null;

    public Rating id(Long id) {
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

    public Rating user(User user) {
        this.user = user;
        return this;
    }

    /**
     * Get user
     *
     * @return user
     **/
    @ApiModelProperty(value = "")

    @Valid
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rating rating(Double rating) {
        this.rating = rating;
        return this;
    }

    /**
     * Percentage rating
     *
     * @return rating
     **/
    @ApiModelProperty(value = "Percentage rating")

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Rating description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @ApiModelProperty(value = "")

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
        Rating rating = (Rating) o;
        return Objects.equals(this.id, rating.id) &&
                Objects.equals(this.user, rating.user) &&
                Objects.equals(this.rating, rating.rating) &&
                Objects.equals(this.description, rating.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, rating, description);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Rating {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    user: ").append(toIndentedString(user)).append("\n");
        sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
