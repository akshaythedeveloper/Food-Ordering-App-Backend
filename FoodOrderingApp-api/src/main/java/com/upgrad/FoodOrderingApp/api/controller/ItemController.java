package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getRestaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantByRestaurantId(restaurant_id); //Getting Restaurant Details based on restaurantID
        List<ItemEntity> itemEntityList = itemService.getItemsByPopularity(restaurantEntity);
        ItemListResponse itemListResponse = new ItemListResponse();
        ItemList itemList = new ItemList();
        List<ItemList> itemLists = new ArrayList<>();
        for(ItemEntity itemEntity:itemEntityList)
        {
            itemList.setId(UUID.fromString(itemEntity.getUuid())); //Setting UUID in ItemList Object
            itemList.setItemName(itemEntity.getItemName()); //Setting Item Name in ItemList Object
            itemList.setPrice(itemEntity.getPrice()); //Setting Price in ItemList Object
            if (itemEntity.getType().equalsIgnoreCase("0")) {
                itemList.setItemType(ItemList.ItemTypeEnum.fromValue("VEG")); //Setting Item Type in ItemList Object
            } else if (itemEntity.getType().equalsIgnoreCase("1")) {
                itemList.setItemType(ItemList.ItemTypeEnum.fromValue("NON_VEG"));//Setting Item Type in ItemList Object
            }
            itemListResponse.add(itemList); //Setting Response object to be returned

        }




        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }

}
