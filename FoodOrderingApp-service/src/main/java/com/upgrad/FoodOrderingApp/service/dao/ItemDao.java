package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves the Item with the matched uuid
     *
     * @param itemUUID The uuid of the Item to be searched for
     * @return The Item Entity present in the Database if found, null otherwise
     */
    public ItemEntity getItemByUUID(String itemUUID) {
        try {
            return entityManager.createNamedQuery("itemByUUID", ItemEntity.class).setParameter("itemUUID", itemUUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Retrieves all the items related to a particular Order based on id
     *
     * @param orderId The id of the order to fetch the list of items
     * @return The list of items linked to an order
     */
    public List<OrderItemEntity> getItemsByOrderId(Integer orderId) {
        return entityManager.createNamedQuery("itemsByOrderId", OrderItemEntity.class).setParameter("id", orderId).getResultList();
    }

    /**
     * This method is used to retrieve list of Category Entity based upon the restaurant uuid and category uuid
     *
     * @param restaurantUuid restaurant uuid
     * @param categoryUuid   category uuid
     * @return List of Category Entities based upon the restaurant uuid and category uuid
     */
    public List<CategoryEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {
        return entityManager.createNamedQuery("itemsByCategoryAndRestaurant", CategoryEntity.class).
                setParameter("categoryUuid", categoryUuid).setParameter("restaurantUuid", restaurantUuid).getResultList();
    }

    /**
     * Retrieves the mostly ordered items and top 5 items will be returned
     *
     * @param restaurantUUID The uuid of the restaurant for which items has to be retrieved
     * @return The list of popular items limit to maximum of 5
     */
    public List<ItemEntity> getItemsByPopularity(String restaurantUUID) {
        return entityManager.createNamedQuery("itemsByPopularity", ItemEntity.class).setParameter("restaurantUUID", restaurantUUID).setMaxResults(5).getResultList();
    }
}
