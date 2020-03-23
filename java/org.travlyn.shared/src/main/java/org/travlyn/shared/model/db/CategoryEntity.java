package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Category;
import org.travlyn.shared.model.api.DataTransferObject;

import javax.persistence.*;

@Entity
@Table(name = "category")
public class CategoryEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

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

    @Override
    public Category toDataTransferObject() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        return category;
    }
}
