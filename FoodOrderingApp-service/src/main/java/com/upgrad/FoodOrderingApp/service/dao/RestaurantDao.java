package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
     * Retrieves the Restaurant Entity with the matched uuid
     *
     * @param restaurantUUID The uuid of the Restaurant to search for
     * @return The Restaurant Entity if found, null otherwise
     */
    public RestaurantEntity getRestaurantByUUID(String restaurantUUID) {
        try {
            return entityManager.createNamedQuery("restaurantByUUID", RestaurantEntity.class).setParameter("restaurantUUID", restaurantUUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
