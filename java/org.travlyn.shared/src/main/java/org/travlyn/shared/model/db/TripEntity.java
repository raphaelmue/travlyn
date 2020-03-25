package org.travlyn.shared.model.db;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "trip")
public class TripEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    @ManyToOne(targetEntity = CityEntity.class)
    private CityEntity city;

    @Column(name = "private")
    private boolean isPrivate;

    @OneToMany(mappedBy = "trip")
    private Set<TripStopEntity> stops;

    @OneToMany()
    @JoinColumn(name = "ratable")
    private Set<TripRatingEntity> ratings;

    @OneToMany(mappedBy = "trip")
    private Set<GeoTextEntity> geoTexts;

    @Override
    public int getId() {
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

    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Set<TripStopEntity> getStops() {
        return stops;
    }

    public void setStops(Set<TripStopEntity> stops) {
        this.stops = stops;
    }

    public Set<TripRatingEntity> getRatings() {
        return ratings;
    }

    public void setRatings(Set<TripRatingEntity> ratings) {
        this.ratings = ratings;
    }

    public Set<GeoTextEntity> getGeoTexts() {
        return geoTexts;
    }

    public void setGeoTexts(Set<GeoTextEntity> geoTexts) {
        this.geoTexts = geoTexts;
    }
}
