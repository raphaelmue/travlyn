package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Stop;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "stop")
public class StopEntity extends Location {

    @Column(name = "pricing")
    private double pricing;

    @Column(name = "time_effort")
    private double timeEffort;

    @Column(name = "time_effort_counter")
    private int numberOfTimeEffortCommitments = 0;

    @Column(name = "average_rating")
    private double averageRating;

    @Column(name = "average_pricing_counter")
    private int numberOfPricingCommitments = 0;

    @ManyToOne(targetEntity = CityEntity.class)
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private CityEntity city;

    @OneToMany
    @JoinColumn(name = "ratable")
    private Set<StopRatingEntity> ratings;


    @OneToOne(targetEntity = CategoryEntity.class, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private CategoryEntity category;

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

    public int getNumberOfTimeEffortCommitments() {
        return numberOfTimeEffortCommitments;
    }

    public void setNumberOfTimeEffortCommitments(int numberOfTimeEffortCommitments) {
        this.numberOfTimeEffortCommitments = numberOfTimeEffortCommitments;
    }

    public int getNumberOfPricingCommitments() {
        return numberOfPricingCommitments;
    }

    public void setNumberOfPricingCommitments(int numberOfRatingCommitments) {
        this.numberOfPricingCommitments = numberOfRatingCommitments;
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
        stop.setId(super.getId());
        stop.setName(super.getName());
        stop.setImage(super.getImage());
        stop.setCategory(this.category.toDataTransferObject());
        stop.setTimeEffort(this.timeEffort);
        stop.setPricing(this.pricing);
        stop.setLatitude(super.getLatitude());
        stop.setLongitude(super.getLongitude());
        stop.setAverageRating(this.averageRating);
        stop.description(super.getDescription());
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
