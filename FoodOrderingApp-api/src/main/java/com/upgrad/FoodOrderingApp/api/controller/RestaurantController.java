package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.business.ItemService;
import com.upgrad.FoodOrderingApp.service.business.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
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

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;

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
        //this will generate the ResponseEntity<RestaurantListResponse>
        return populateRestaurantList(restaurantEntityList);
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
    public ResponseEntity<RestaurantListResponse> restaurantsByName(@PathVariable(name = "restaurant_name", required = false)
                                                                            String restaurantName) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByName(restaurantName);
        //this will generate the ResponseEntity<RestaurantListResponse>
        return populateRestaurantList(restaurantEntityList);
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
        List<RestaurantEntity> restaurantsListByCategory = restaurantService.restaurantByCategory(categoryId);
        //this will generate the ResponseEntity<RestaurantListResponse>
        return populateRestaurantList(restaurantsListByCategory);
    }


    /**
     * This method is used find list of  all the restaurants based upon restaurant uuid passed
     * This method request restaurant uuid from the customer as a path variable
     *
     * @param restaurantUuid The restaurant uuid passed in the path variable
     * @return Restaurant Detail Response with Http status
     * @throws RestaurantNotFoundException If the restaurant id field entered by the customer is empty or
     *                                     If there is no restaurant by the uuid entered by the customer
     */
    @RequestMapping(method = RequestMethod.GET, path = "restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> restaurantByUUID(@PathVariable(name = "restaurant_id", required = false)
                                                                              String restaurantUuid) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        RestaurantDetailsResponse restaurantDetailsResponse = populateRestaurantDetailsResponse(restaurantEntity);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);

    }

    /**
     * This method is used to update the average customer rating of a particular restaurant
     * Here the customer_rating should be between 1 and 5 (both inclusive)
     *
     * @param authorization  The Bearer authorization token from the headers
     * @param restaurantUuid The restaurant uuid of the restaurant for which the rating has to be updated
     * @param customerRating The customer rating passed by the customer
     * @return
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     * @throws RestaurantNotFoundException  If the restaurant id field entered by the customer is empty
     *                                      or If there is no restaurant by the uuid entered by the customer
     * @throws InvalidRatingException       If the customer rating field entered by the customer is empty or is not in the range of 1 to 5
     */
    @RequestMapping(method = RequestMethod.PUT, path = {"/restaurant/{restaurant_id}", "/restaurant"})
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantRating(@RequestHeader("authorization") final String authorization,
                                                                            @PathVariable(name = "restaurant_id", required = false) String restaurantUuid,
                                                                            @RequestParam(name = "customer_rating") Double customerRating
    )
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {
        CustomerEntity customer = customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
        restaurantUpdatedResponse.setId(UUID.fromString(restaurantUuid));
        restaurantUpdatedResponse.setStatus("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    /**
     * Populate ResponseEntity of type RestaurantListResponse which consists of restaurant list retrieved from data base
     *
     * @param restaurantEntityList The list of restaurants pulled from Database
     * @return Response Entity of type RestaurantListResponse
     */
    private ResponseEntity<RestaurantListResponse> populateRestaurantList(List<RestaurantEntity> restaurantEntityList) {

        List<RestaurantList> restaurantsList = new ArrayList<RestaurantList>();


        // If restaurantEntityList is empty than just return empty array with HTTP status OK
        if (restaurantEntityList == null || restaurantEntityList.isEmpty()) {
            RestaurantListResponse response = new RestaurantListResponse();
            response.setRestaurants(restaurantsList);
            return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);

        }

        for (RestaurantEntity restaurantEntity : restaurantEntityList) {
            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

            // For each restaurant, retrieve the list of categories
            restaurantEntity.setCategories(categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid()));

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
        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(restaurantsList);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    /**
     * This method gets the Restaurant Entity and converts to RestaurantDetailResponse
     *
     * @param restaurantEntity The restaurant Entity fetched from database
     * @return Restaurant Detail Response
     */
    private RestaurantDetailsResponse populateRestaurantDetailsResponse(RestaurantEntity restaurantEntity) {
        // Frame the address in response
        AddressEntity restaurantAddress = restaurantEntity.getAddress();

        RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

        responseAddress.id(UUID.fromString(restaurantAddress.getUuid())).flatBuildingName(restaurantAddress.getFlatBuilNo())
                .locality(restaurantAddress.getLocality()).city(restaurantAddress.getCity()).pincode(restaurantAddress.getPincode());


        RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState();
        state.id(UUID.fromString(restaurantAddress.getState().getUuid())).stateName(restaurantAddress.getState().getStateName());
        responseAddress.state(state);

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();
        // Add restaurant details to the response object
        restaurantDetailsResponse.id(UUID.fromString(restaurantEntity.getUuid())).restaurantName(restaurantEntity.getRestaurantName())
                .address(responseAddress).photoURL(restaurantEntity.getPhotoUrl()).customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                .averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated());

        // Get the list of all categories for the restaurant uuid passed
        List<CategoryEntity> restaurantCategories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());

        // Frame category List Response
        List<CategoryList> categoryListArrayList = new ArrayList<>();
        for (CategoryEntity restaurantCategory : restaurantCategories) {
            // Creating a category list
            CategoryList categoryList = new CategoryList();
            // Frame category list
            categoryList.id(UUID.fromString(restaurantCategory.getUuid())).categoryName(restaurantCategory.getCategoryName());
            // Getting List of Item Entities from data base
            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantEntity.getUuid(), restaurantCategory.getUuid());
            // Frame Item list
            List<ItemList> itemListArrayList = new ArrayList<>();
            for (ItemEntity itemEntity : itemEntities) {
                ItemList itemList = new ItemList();
                itemList.id(UUID.fromString(itemEntity.getUuid())).itemName(itemEntity.getItemName()).price(itemEntity.getPrice()).
                        itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().toString()));
                itemListArrayList.add(itemList);
            }
            categoryList.itemList(itemListArrayList);
            // Add categoryList to List<categoryList>
            categoryListArrayList.add(categoryList);
        }
        restaurantDetailsResponse.categories(categoryListArrayList);

        return restaurantDetailsResponse;
    }
}