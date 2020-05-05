package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.DataTransferObject;
import org.travlyn.shared.model.api.Preference;

import javax.persistence.*;

@Entity
@Table(name = "preferences")
public class PreferenceEntity implements DataEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    @ManyToOne(targetEntity = CategoryEntity.class)
    private CategoryEntity categoryEntity;


    @Override
    public int getId() {
        return this.id;
    }

    public PreferenceEntity setId(int id){
        this.id = id;
        return this;
    }
    public UserEntity getUser() {
        return user;
    }

    public PreferenceEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public PreferenceEntity setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
        return this;
    }

    @Override
    public Preference toDataTransferObject() {
        return new Preference()
                    .setCategory(this.categoryEntity.toDataTransferObject())
                    .setId(this.id);
    }
}
