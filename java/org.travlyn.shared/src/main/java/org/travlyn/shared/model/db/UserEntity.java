package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Preference;
import org.travlyn.shared.model.api.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class UserEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @OneToMany(mappedBy = "user")
    private Set<TokenEntity> tokens;

    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE},mappedBy = "user",fetch = FetchType.EAGER)
    private Set<PreferenceEntity> preferences = new HashSet<>();

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public UserEntity setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public Set<TokenEntity> getTokens() {
        return tokens;
    }

    public void setTokens(Set<TokenEntity> tokens) {
        this.tokens = tokens;
    }

    public Set<PreferenceEntity> getPreferences() {
        return preferences;
    }

    public UserEntity setPreferences(Set<PreferenceEntity> preferences) {
        this.preferences = preferences;
        return this;
    }

    @Override
    public User toDataTransferObject() {
        Set<Preference> preferences = new HashSet<>();
        this.preferences.forEach(preferenceEntity -> preferences.add(preferenceEntity.toDataTransferObject()));
        return new User().id(this.id).email(this.email).name(this.name).token(null).setPreferences(preferences);
    }
}
