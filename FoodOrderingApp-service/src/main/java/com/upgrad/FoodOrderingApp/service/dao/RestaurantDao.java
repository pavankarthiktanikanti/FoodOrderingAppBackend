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

    /**
     * Retrieves the list of all the Restaurants where the name field entered by the customer is partially matching
     * Also the name searched is not be case sensitive
     *
     * @param likeRestaurantName concatinated restaurant name %restaurantName%
     * @return list of Restaurant Entity where the name field entered by the customer is partially matching
     */
    public List<RestaurantEntity> restaurantsByName(String likeRestaurantName) {
        return entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class).
                setParameter("likeRestaurantName", likeRestaurantName).getResultList();
    }

    /**
     * This method is used to find the list of restaurants having same category uuid as passed in the request
     *
     * @param uuid The category uuid based upon which restaurants will be fetched from database
     * @return List of Restaurants
     */
    public List<RestaurantEntity> restaurantByCategory(String uuid) {
        return entityManager.createNamedQuery("restaurantsByCategory", RestaurantEntity.class).
                setParameter("uuid", uuid).getResultList();
    }
}
