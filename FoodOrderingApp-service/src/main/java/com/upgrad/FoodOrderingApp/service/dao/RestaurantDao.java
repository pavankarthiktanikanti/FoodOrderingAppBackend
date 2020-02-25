package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieve all the restaurants by descending order of their ratings
     *
     * @return The list of sorted restaurants from the Database
     */
    public List<RestaurantEntity> restaurantsByRating() {
        return entityManager.createNamedQuery("restaurantsByRating", RestaurantEntity.class).getResultList();
    }
}