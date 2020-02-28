package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

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
}
