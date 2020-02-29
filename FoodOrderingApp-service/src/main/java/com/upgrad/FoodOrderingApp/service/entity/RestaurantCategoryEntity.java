package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RESTAURANT_CATEGORY")
@NamedQueries({
        @NamedQuery(name = "categoriesByRestaurantUUID", query = "select r from RestaurantCategoryEntity r where r.restaurant.uuid = :restaurantUUID order by r.category.categoryName"),
        @NamedQuery(name = "restaurantsByCategory", query = "select r.restaurant from RestaurantCategoryEntity r where r.category.uuid =:uuid "),
        @NamedQuery(name = "itemsByCategoryAndRestaurant", query = "select r.category from RestaurantCategoryEntity r where r.category.uuid = :categoryUuid and r.restaurant.uuid = :restaurantUuid")
})
public class RestaurantCategoryEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id")
    @NotNull
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id")
    @NotNull
    private CategoryEntity category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
