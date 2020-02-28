package com.upgrad.FoodOrderingApp.service.dao;

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
}
