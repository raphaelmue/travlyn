package org.travlyn.shared.model.db;

import javax.persistence.*;

@MappedSuperclass
public abstract class RatingEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    @Column(name = "rating")
    private double rating;

    @Column(name = "description")
    private String description;

    @Override
    public final int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
