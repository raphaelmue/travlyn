package org.travlyn.shared.model.db;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
public class StopRatingEntity extends RatingEntity {

    @ManyToOne(targetEntity = StopEntity.class)
    private StopEntity stop;

    public StopEntity getStop() {
        return stop;
    }

    public void setStop(StopEntity stop) {
        this.stop = stop;
    }
}
