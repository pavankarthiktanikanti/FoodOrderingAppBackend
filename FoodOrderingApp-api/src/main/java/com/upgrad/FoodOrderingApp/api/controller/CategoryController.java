package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * This method retrieves the category with all the items matching that category based on the uuid of category
     * No authorization required for this endpoint
     *
     * @param categoryUUID The uuid of the category to be retrieved
     * @return The category with all the items under it
     * @throws CategoryNotFoundException If the category uuid is not matched with any of the records in Database
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/category/{category_id}")
    public ResponseEntity<CategoryDetailsResponse> getCategoryDetails(@PathVariable("category_id") String categoryUUID)
            throws CategoryNotFoundException {
        CategoryEntity category = categoryService.getCategoryById(categoryUUID);

        List<ItemEntity> categoryItems = category.getItems();
        List<ItemList> itemsList = new ArrayList<ItemList>();
        // If any items exists under a category, populate in the response
        if (categoryItems != null && !categoryItems.isEmpty()) {
            categoryItems.forEach(item -> {
                ItemList itemList = new ItemList();
                itemList.id(UUID.fromString(item.getUuid())).itemName(item.getItemName()).price(item.getPrice())
                        .itemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
                itemsList.add(itemList);
            });
        }
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        categoryDetailsResponse.itemList(itemsList);
        categoryDetailsResponse.id(UUID.fromString(category.getUuid())).categoryName(category.getCategoryName());
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}
