package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.business.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@CrossOrigin
@RestController
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    /**
     * This method returns the list of all available restaurants with the details
     * No authorization required for this endpoint
     *
     * @return The list of all available restaurants with details sorted by rating descending
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        // Read restaurants ordered by the ratings of each restaurant
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByRating();
        if (restaurantEntityList != null) {
            // For each restaurant, retrieve the list of categories
            for (RestaurantEntity restaurant : restaurantEntityList) {
                restaurant.setCategories(categoryService.getCategoriesByRestaurant(restaurant.getUuid()));
            }
        }
        List<RestaurantList> restaurantList = populateRestaurantList(restaurantEntityList);
        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(restaurantList);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    /**
     * This method is used to find the restaurant List partially matching the restaurant name passed in request
     * Also the name searched is not be case sensitive
     *
     * @param restaurantName The restaurant name field send in the resquest
     * @return List of all restaurant partially matching the name passed in alphabetic order of restraunt name
     * @throws RestaurantNotFoundException If restaurant name field entered by the customer is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = {"/restaurant/name/{restaurant_name}", "/restaurant/name"},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantsByName(@PathVariable(name = "reastaurant_name", required = false)
                                                                            String restaurantName) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByName(restaurantName);
        if (restaurantEntityList != null) {
            // For each restaurant, retrieve the list of categories
            for (RestaurantEntity restaurant : restaurantEntityList) {
                restaurant.setCategories(categoryService.getCategoriesByRestaurant(restaurant.getUuid()));
            }
        }
        List<RestaurantList> restaurantList = populateRestaurantList(restaurantEntityList);
        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(restaurantList);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }


    /**
     * This method is used to get restaurants based upon the category uuid
     * This method requests the category uuid as string from the customer as a path variable
     *
     * @param categoryId The category uuid based upon which restaurants will be fetched from database
     * @return List all restaurants having same catergory uuid
     * @throws CategoryNotFoundException If the category id field entered by the customer is empty or
     *                                   If there is no category by the uuid entered by the customer
     */
    @RequestMapping(method = RequestMethod.GET, path = {"/restaurant/category/{category_id}", "/restaurant/category"},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantByCategory(@PathVariable(name = "category_id", required = false)
                                                                               String categoryId) throws CategoryNotFoundException {
        List<RestaurantEntity> restaurantsListByUuid = restaurantService.restaurantByCategory(categoryId);
        if (restaurantsListByUuid != null) {
            for (RestaurantEntity restaurant : restaurantsListByUuid) {
                restaurant.setCategories(categoryService.getCategoriesByRestaurant(restaurant.getUuid()));
            }
        }
        List<RestaurantList> restaurantsList = populateRestaurantList(restaurantsListByUuid);
        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(restaurantsList);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }


    /**
     * Populate the list of restaurant responses to be sent for the request from the list of
     * restaurants retrieved from Database
     *
     * @param restaurantEntityList The list of restaurants pulled from Database
     * @return The restaurant List to be added to the Reponse Entity for the clients
     */
    private List<RestaurantList> populateRestaurantList(List<RestaurantEntity> restaurantEntityList) {
        // If no records returned from database, just add an empty list in the response
        if (restaurantEntityList == null || restaurantEntityList.isEmpty()) {
            return new ArrayList<RestaurantList>();
        }
        List<RestaurantList> restaurantsList = new ArrayList<RestaurantList>();
        for (RestaurantEntity restaurantEntity : restaurantEntityList) {
            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

            // Frame the address in response
            AddressEntity restaurantAddress = restaurantEntity.getAddress();
            responseAddress.id(UUID.fromString(restaurantAddress.getUuid())).flatBuildingName(restaurantAddress.getFlatBuilNo())
                    .locality(restaurantAddress.getLocality()).city(restaurantAddress.getCity()).pincode(restaurantAddress.getPincode());

            // Frame the state details in the response
            RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState();
            state.id(UUID.fromString(restaurantAddress.getState().getUuid())).stateName(restaurantAddress.getState().getStateName());
            responseAddress.state(state);

            RestaurantList restaurantList = new RestaurantList();
            // Add restaurant details to the response object
            restaurantList.id(UUID.fromString(restaurantEntity.getUuid())).restaurantName(restaurantEntity.getRestaurantName())
                    .address(responseAddress).photoURL(restaurantEntity.getPhotoUrl()).customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated());

            List<CategoryEntity> restaurantCategories = restaurantEntity.getCategories();
            StringBuilder sb = new StringBuilder();
            // Iterate to add list of categories combined to a single String separated by , and space
            for (int index = 0; index < restaurantCategories.size(); index++) {
                sb.append(restaurantCategories.get(index).getCategoryName());
                if (index < restaurantCategories.size() - 1) {
                    sb.append(",").append(" ");
                }
            }
            restaurantList.categories(sb.toString());
            restaurantsList.add(restaurantList);
        }
        return restaurantsList;
    }
}