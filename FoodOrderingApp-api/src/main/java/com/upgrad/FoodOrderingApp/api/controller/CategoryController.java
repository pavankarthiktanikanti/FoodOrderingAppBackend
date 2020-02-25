package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Retrieves the List of all Categories available in the Database
     * Returns the UUID and name of each Category
     * No authorization required for this endpoint
     *
     * @return The list of categories with uuid and name for each category
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/category")
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        List<CategoryEntity> categories = categoryService.getAllCategoriesOrderedByName();
        List<CategoryListResponse> categoryListResponses = new ArrayList<CategoryListResponse>();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        // Check if the Database records has any categories or not
        if (categories != null && !categories.isEmpty()) {
            for (CategoryEntity category : categories) {
                CategoryListResponse categoryListResponse = new CategoryListResponse();
                categoryListResponse.id(UUID.fromString(category.getUuid())).categoryName(category.getCategoryName());
                categoriesListResponse.addCategoriesItem(categoryListResponse);
            }
        }
        if (categoriesListResponse.getCategories() == null) {
            categoriesListResponse.categories(new ArrayList<CategoryListResponse>());
        }
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }
}
