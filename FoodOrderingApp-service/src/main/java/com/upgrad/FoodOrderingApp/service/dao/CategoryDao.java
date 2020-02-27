package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves the list of categories from Database ordered by category name
     *
     * @return The sorted list of categories by name
     */
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return entityManager.createNamedQuery("allCategoriesOrderedByName", CategoryEntity.class).getResultList();
    }

    /**
     * Retrieves the matched Category Record from the Database based on the uuid
     *
     * @param categoryUUID The uuid of the category to be retrieved from Database
     * @return The category entity will all the item details under it
     */
    public CategoryEntity getCategoryByUUID(String categoryUUID) {
        try {
            return entityManager.createNamedQuery("categoryByUUID", CategoryEntity.class).setParameter("uuid", categoryUUID).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
