package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * Retrieves the Item Entity based on the uuid passed
     * Throws error message if the uuid doesn't match with any records
     *
     * @param itemId The uuid of the item to search for
     * @return The matched Item Entity retrieved from database
     * @throws ItemNotFoundException If the uuid doesn't match with any Database records
     */
    public ItemEntity getItemByUUID(UUID itemId) throws ItemNotFoundException {
        if (itemId != null) {
            ItemEntity item = itemDao.getItemByUUID(itemId.toString());
            if (item != null) {
                return item;
            }
        }
        throw new ItemNotFoundException("INF-003", "No item by this id exist");
    }

    /**
     * Retrieves the popular items from the Database for a particular restaurant
     *
     * @param restaurant The restaurant entity for which items has to be pulled
     * @return The list of popular items
     */
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurant) {
        return itemDao.getItemsByPopularity(restaurant.getUuid());
    }

    /**
     * This method filters the list of restaurant items based on the category passed
     *
     * @param restaurantUuid The restaurant uuid for which items has to be retrieved
     * @param categoryUuid   The category uuid for which items has to be retrieved
     * @return The list of restaurant items matched with the category
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {
        // Get Items based on restaurant uuid
        List<ItemEntity> itemsOfRestaurant = itemDao.getItemsByRestaurant(restaurantUuid);
        // Get Items based on category uuid
        List<ItemEntity> categoryItems = itemDao.getItemsByCategory(categoryUuid);
        List<ItemEntity> categoryItemsOfRestaurant = new ArrayList<ItemEntity>();
        if (itemsOfRestaurant != null) {
            itemsOfRestaurant.forEach(item -> {
                if (categoryItems != null) {
                    for (ItemEntity categoryItem : categoryItems) {
                        // Check if the item belongs to one of the items in this category
                        if (item.getId() == categoryItem.getId()) {
                            categoryItemsOfRestaurant.add(item);
                            break;
                        }
                    }
                }
            });
        }
        return categoryItemsOfRestaurant;
    }
}
