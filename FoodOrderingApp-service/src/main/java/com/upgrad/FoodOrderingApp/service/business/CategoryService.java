package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
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
     * @return The list of categories with uui and name of each category
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
}
