package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantCategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Queries the Database with the restaurant uuid and returns all the categories related to the restaurant
     *
     * @param restaurantUUID The uuid of the restaurant for which categories has to be retrieved
     * @return The list of Restaurant Categories matched with the uui of restaurant
     */
    public List<RestaurantCategoryEntity> getRestaurantCategoriesByRestaurantUUID(String restaurantUUID) {
        return entityManager.createNamedQuery("categoriesByRestaurantUUID", RestaurantCategoryEntity.class).setParameter("restaurantUUID", restaurantUUID).getResultList();
    }
}