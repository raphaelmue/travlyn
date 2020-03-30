package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Stop;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "stop")
public class StopEntity implements DataEntity {

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

    @Column(name = "description", length = 65536)
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "pricing")
    private double pricing;

    @Column(name = "time_effort")
    private double timeEffort;

    @Column(name = "average_rating")
    private double averageRating;

    @ManyToOne(targetEntity = CityEntity.class)
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private CityEntity city;

    @OneToMany
    @JoinColumn(name = "ratable")
    private Set<StopRatingEntity> ratings;

    @OneToOne(targetEntity = CategoryEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private CategoryEntity category;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPricing() {
        return pricing;
    }

    public void setPricing(double pricing) {
        this.pricing = pricing;
    }

    public double getTimeEffort() {
        return timeEffort;
    }

    public void setTimeEffort(double timeEffort) {
        this.timeEffort = timeEffort;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public Set<StopRatingEntity> getRatings() {
        return ratings;
    }

    public void setRatings(Set<StopRatingEntity> ratings) {
        this.ratings = ratings;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    @Override
    public Stop toDataTransferObject() {
        Stop stop = new Stop();
        stop.setId(this.id);
        stop.setName(this.name);
        stop.setCategory(this.category.toDataTransferObject());
        stop.setTimeEffort(this.timeEffort);
        stop.setPricing(this.pricing);
        stop.setLatitude(this.latitude);
        stop.setLongitude(this.longitude);
        stop.setAverageRating(this.averageRating);
        stop.description(this.description);
        //stop.city(this.city.toDataTransferObject());
        //TODO
        //stop.setRatings(this.ratings);
        return stop;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public CityEntity getCity() {
        return city;
    }
}
