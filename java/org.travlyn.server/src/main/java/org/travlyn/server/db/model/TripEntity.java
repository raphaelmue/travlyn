package org.travlyn.server.db.model;

import javax.persistence.*;
import java.util.List;
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

    @OneToMany(mappedBy = "entity")
    private List<RatingEntity> ratings;

    @OneToMany(mappedBy = "stop")
    private List<GeoTextEntity> geoTexts;

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

    public List<RatingEntity> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingEntity> ratings) {
        this.ratings = ratings;
    }

    public List<GeoTextEntity> getGeoTexts() {
        return geoTexts;
    }

    public void setGeoTexts(List<GeoTextEntity> geoTexts) {
        this.geoTexts = geoTexts;
    }
}
