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

    /**
     * Retrives the list of all the Restaurants where the name field entered by the customer is partially matching
     * Also the name searched is not be case sensitive
     *
     * @param likeRestaurantName concatinated restaurant name %restaurantName%
     * @return list of Restaurant Entity where the name field entered by the customer is partially matching
     */
    public List<RestaurantEntity> restaurantsByName(String likeRestaurantName) {
        return entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class).
                setParameter("likeRestaurantName", likeRestaurantName).getResultList();
    }
}
