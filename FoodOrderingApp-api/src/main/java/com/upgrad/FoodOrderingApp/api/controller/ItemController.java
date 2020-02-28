package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.business.ItemService;
import com.upgrad.FoodOrderingApp.service.business.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class ItemController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    /**
     * Look up the top 5 items based on the popularity of item
     * Retrieves the list of popular items grouped by the number of times it is ordered
     * No authorization required for this endpoint
     *
     * @param restaurantId The uuid of the restaurant for which items has to be retrieved
     * @return The List of top 5 items
     * @throws RestaurantNotFoundException If the uuid of restaurant doesn't match with database records
     */
    @RequestMapping(method = RequestMethod.GET,
            path = "/item/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getTop5ItemsByPopularity(
            @PathVariable(value = "restaurant_id") String restaurantId)
            throws RestaurantNotFoundException {
        // Retrieve the restaurant entity using uuid of restaurant
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantId);

        // Pull the top 5 popular items for the restaurant
        List<ItemEntity> popularItems = itemService.getItemsByPopularity(restaurant);
        ItemListResponse response = new ItemListResponse();
        if (popularItems != null) {
            // Iterate through the popular items and set the item details in response
            popularItems.stream().forEach(item -> {
                ItemList itemList = new ItemList();
                itemList.id(UUID.fromString(item.getUuid())).itemName(item.getItemName()).price(item.getPrice())
                        .itemType(ItemList.ItemTypeEnum.valueOf(item.getType().getValue()));
                response.add(itemList);
            });
        }

        return new ResponseEntity<ItemListResponse>(response, HttpStatus.OK);
    }
}
