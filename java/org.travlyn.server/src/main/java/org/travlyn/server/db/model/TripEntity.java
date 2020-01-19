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

    @ManyToMany
    @JoinTable(name = "trip_stop",
            joinColumns = {@JoinColumn(name = "trip_id")},
            inverseJoinColumns = {@JoinColumn(name = "stop_id")})
    private Set<StopEntity> stops;

    @OneToMany(mappedBy = "stop")
    private List<RatingEntity> ratings;

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

    public Set<StopEntity> getStops() {
        return stops;
    }

    public void setStops(Set<StopEntity> stops) {
        this.stops = stops;
    }

    public List<RatingEntity> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingEntity> ratings) {
        this.ratings = ratings;
    }
}
