package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.travlyn.shared.model.db.DataEntity;
import org.travlyn.shared.model.db.PreferenceEntity;

public class Preference extends AbstractDataTransferObject{

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("user")
    @ApiModelProperty(value = "User", required = true)
    private User user = null;

    @JsonProperty("id")
    @ApiModelProperty(value = "Category", required = true)
    private Category category = null;

    public int getId() {
        return id;
    }

    public Preference setId(int id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Preference setUser(User user) {
        this.user = user;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Preference setCategory(Category category) {
        this.category = category;
        return this;
    }

    @Override
    public PreferenceEntity toEntity() {
        return new PreferenceEntity()
                    .setCategoryEntity(category.toEntity())
                    .setUser(user.toEntity())
                    .setId(this.id);
    }
}
