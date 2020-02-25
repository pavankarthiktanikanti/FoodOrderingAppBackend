package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
}
