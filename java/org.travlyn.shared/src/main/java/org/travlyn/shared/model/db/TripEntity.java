package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.api.Trip;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "trip", cascade = {CascadeType.ALL})
    private Set<TripStopEntity> stops;

    @OneToMany()
    @JoinColumn(name = "ratable")
    private Set<TripRatingEntity> ratings;

    @OneToMany(mappedBy = "trip", cascade = {CascadeType.ALL})
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Trip toDataTransferObject() {
        Trip trip = new Trip();
        trip.setId(this.id);
        if (this.city != null) {
            trip.setCity(this.city.toDataTransferObject());
        }
        trip.setPrivate(this.isPrivate);
        trip.setUser(this.user.toDataTransferObject());
        trip.name(this.name);
        List<Stop> stops = new ArrayList<>();
        TripStopEntity predecessor = null;
        while (!this.stops.isEmpty()) {
            for (Iterator<TripStopEntity> i = this.stops.iterator(); i.hasNext(); ) {
                TripStopEntity tripStopEntity = i.next();
                if (tripStopEntity.getPredecessor() == null || (tripStopEntity.getPredecessor() != null && tripStopEntity.getPredecessor().equals(predecessor))) {
                    stops.add(tripStopEntity.toDataTransferObject());
                    predecessor = tripStopEntity;
                    i.remove();
                }
            }
        }
        trip.setStops(stops);
        //TODO
        trip.setRatings(new ArrayList<>());
        trip.setGeoText(new ArrayList<>());
        return trip;
    }
}
