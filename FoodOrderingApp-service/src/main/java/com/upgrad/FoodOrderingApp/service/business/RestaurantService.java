package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    /**
     * Retrieves the list of available Restaurants ordered by rating descending
     *
     * @return The restaurants in sorted descending order by rating
     */
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurants = restaurantDao.restaurantsByRating();
        return restaurants;
    }

    /**
     * This method retrieves the Restaurant Details record with the matched uuid
     *
     * @param restaurantUUID The uuid of restaurant to search for
     * @return The Restaurant Details retrieved from Database
     * @throws RestaurantNotFoundException If the uuid doesn't match with any database records
     */
    public RestaurantEntity restaurantByUUID(String restaurantUUID) throws RestaurantNotFoundException {
        RestaurantEntity restaurant = restaurantDao.getRestaurantByUUID(restaurantUUID);
        if (restaurant != null) {
            return restaurant;
        }
        // If no restaurant available with the uuid or when input doesn't have the uuid details
        throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
    }
}