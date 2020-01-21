package org.travlyn.shared.model.db;

import javax.persistence.*;

@Entity
@Table(name = "geo_text")
public class GeoTextEntity implements DataEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(targetEntity = StopEntity.class)
    private StopEntity stop;

    @Column(name = "text")
    private String text;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StopEntity getStop() {
        return stop;
    }

    public void setStop(StopEntity stop) {
        this.stop = stop;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
