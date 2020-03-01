package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RestaurantCategoryDao restaurantCategoryDao;

    /**
     * Retrieves the List of Categories from the Database order by name
     *
     * @return The list of categories with uuid and name of each category
     */
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategoriesOrderedByName();
    }

    /**
     * This method fetches the list of all categories based on the restaurant uuid
     *
     * @param restaurantUUID The uuid of the restaurant to search for categories in Database
     * @return The list of categories matched with the restaurant uuid
     */
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUUID) {
        List<RestaurantCategoryEntity> restaurantCategories = restaurantCategoryDao.getRestaurantCategoriesByRestaurantUUID(restaurantUUID);
        List<CategoryEntity> categories = new ArrayList<CategoryEntity>();
        if (restaurantCategories != null) {
            restaurantCategories.forEach(restaurantCategory -> {
                categories.add(restaurantCategory.getCategory());
            });
        }
        return categories;
    }

    /**
     * Retrieves the Category details based on the category uuid
     * Throws error message if in case there is no match found with the uuid in Database
     *
     * @param categoryUUID The uuid of the category to be retrieved
     * @return The Category with all item details under it from the Database
     * @throws CategoryNotFoundException If the uuid passed is empty or not found in Database
     */
    public CategoryEntity getCategoryById(String categoryUUID) throws CategoryNotFoundException {
        // If uuid passed is empty or null
        if (FoodOrderingUtil.isInValid(categoryUUID)) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity category = categoryDao.getCategoryByUUID(categoryUUID);
        // No match found in the Database for the uuid
        if (category == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return category;
    }
}
