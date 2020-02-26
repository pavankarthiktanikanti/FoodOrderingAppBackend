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
     * Retrives the list of all the Restaurants where the name field entered by the customer is partially matching
     * It will first check if the name field entered by customer is empty or not
     *
     * @param restaurantName The restaurant name send in the request
     * @return list of Restaurant Entity where the name field entered by the customer is partially matching
     * @throws RestaurantNotFoundException When the name field entered by cutomer is empty
     */
    public List<RestaurantEntity> restaurantsByName(String restaurantName) throws RestaurantNotFoundException {
        // checks if restuarant name field entered by customer is empty or not
        if (restaurantName.isEmpty() || restaurantName == null) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        //here we are concating restuarant name passed in request so that it become like %restaurantName%
        String likeRestaurantName = "".concat("%").concat(restaurantName).concat("%");
        List<RestaurantEntity> restaurants = restaurantDao.restaurantsByName(likeRestaurantName);
        return restaurants;
    }
}