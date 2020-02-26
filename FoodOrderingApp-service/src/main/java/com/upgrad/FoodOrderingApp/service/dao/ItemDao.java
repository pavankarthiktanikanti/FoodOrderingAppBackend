package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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
}
