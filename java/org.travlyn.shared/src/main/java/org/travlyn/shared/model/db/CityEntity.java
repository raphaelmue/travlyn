package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Token;

import javax.persistence.*;

@Entity
@Table(name = "city")
public class CityEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "description")
    private String description;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public City toDataTransferObject() {
        return new City().id(this.id).name(this.name).image(this.image).description(this.description);
    }
}
