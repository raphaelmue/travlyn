package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "city")
public class CityEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "description", length = 65536)
    private String description;

    @Column(name = "unfetched_stops")
    private boolean unfetchedStops;

    @OneToMany(orphanRemoval = true,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<StopEntity> stops;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<StopEntity> getStops() {
        return stops;
    }

    public void setStops(Set<StopEntity> stops) {
        this.stops = stops;
    }

    public boolean isUnfetchedStops() {
        return unfetchedStops;
    }

    public void setUnfetchedStops(boolean unfetchedStops) {
        this.unfetchedStops = unfetchedStops;
    }

    @Override
    public City toDataTransferObject() {
        City city = new City()
                .id(this.id)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .name(this.name)
                .image(this.image)
                .description(this.description)
                .setUnfetchedStops(this.unfetchedStops);
        HashSet<Stop> stopHashSet = new HashSet<>();
        for (StopEntity stopEntity : stops) {
            stopHashSet.add(stopEntity.toDataTransferObject());
        }
        city.setStops(stopHashSet);
        return city;
    }
}
