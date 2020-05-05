package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "city")
public class CityEntity extends Location{

    @Column(name = "unfetched_stops")
    private boolean unfetchedStops;

    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<StopEntity> stops = new HashSet<>();

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
                .id(super.getId())
                .longitude(super.getLongitude())
                .latitude(super.getLatitude())
                .name(super.getName())
                .image(super.getImage())
                .description(super.getDescription())
                .setUnfetchedStops(this.unfetchedStops);
        List<Stop> stopHashSet = new ArrayList<>();
        for (StopEntity stopEntity : stops) {
            stopHashSet.add(stopEntity.toDataTransferObject());
        }
        city.setStops(stopHashSet);
        return city;
    }
}
