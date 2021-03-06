package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "CATEGORY")
@NamedQueries({
        @NamedQuery(name = "allCategoriesOrderedByName", query = "select c from CategoryEntity c order by c.categoryName"),
        @NamedQuery(name = "categoryByUUID", query = "select c from CategoryEntity c where c.uuid = :uuid")
})
public class CategoryEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "category_name")
    @Size(max = 255)
    private String categoryName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_ITEM", joinColumns = {
            @JoinColumn(name = "category_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "item_id",
                    nullable = false)})
    private List<ItemEntity> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }
}
