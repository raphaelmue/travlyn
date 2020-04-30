package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.PreferenceEntity;
import org.travlyn.shared.model.db.UserEntity;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User
 */
@Validated
public class User extends AbstractDataTransferObject {
    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("email")
    @ApiModelProperty(value = "Email address", required = true, example = "test@email.com")
    private String email = null;

    @JsonProperty("name")
    @ApiModelProperty(value = "Name", required = true, example = "Test User")
    private String name = null;

    @JsonProperty("token")
    @ApiModelProperty(value = "Active Token for communication", required = true)
    private Token token = null;

    @JsonProperty("preferences")
    @ApiModelProperty(value = "List of preferences of user", required = true)
    @Valid
    private Set<Preference> preferences = null;

    public User id(int id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Get email
     *
     * @return email
     **/
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User token(Token token) {
        this.token = token;
        return this;
    }

    /**
     * Get token
     *
     * @return token
     **/
    @Valid
    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public User setPreferences(Set<Preference> preferences) {
        this.preferences = preferences;
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(this.id, user.id) &&
                Objects.equals(this.email, user.email) &&
                Objects.equals(this.name, user.name) &&
                Objects.equals(this.token, user.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, token);
    }

    @Override
    public UserEntity toEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(this.id);
        userEntity.setEmail(this.email);
        userEntity.setName(this.name);
        //userEntity.setTokens(new HashSet<>(Collections.singletonList(this.token.toEntity())));
        Set<PreferenceEntity> preferenceEntities = new HashSet<>();
        this.preferences.forEach(preference -> preferenceEntities.add(preference.toEntity()));
        userEntity.setPreferences(preferenceEntities);
        return userEntity;
    }
}
