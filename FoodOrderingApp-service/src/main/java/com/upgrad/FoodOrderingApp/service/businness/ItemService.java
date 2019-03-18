package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RestaurantDao restaurantDao;

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String someRestaurantId, String uuid) {
        List<ItemEntity> itemEntities = new ArrayList<>();
        return itemEntities;
    }

    public ItemEntity getItemById(String itemId) {
       ItemEntity itemEntity = itemDao.getItemById(itemId);
       return itemEntity;
    }

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<OrdersEntity> ordersEntities = orderDao.getOrderByRestaurant(restaurantEntity);
        List<RestaurantItemEntity> restaurantItemEntities = restaurantDao.getItemsByRestaurant(restaurantEntity);
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        HashMap<ItemEntity,Integer> itemEntityOrdersEntityHashMap = new HashMap<>();
       // List<OrderItemEntity> orderItemEntities1 = new ArrayList<>();
        for(OrdersEntity ordersEntity:ordersEntities)
        {
            for(RestaurantItemEntity restaurantItemEntity:restaurantItemEntities)
            {
                orderItemEntities = itemDao.getItemByOrder(ordersEntity,restaurantItemEntity.getItemId());
                for(OrderItemEntity orderItemEntity:orderItemEntities)
                {
                    itemEntities.add(orderItemEntity.getItemId());
                }
            }

        }


        return  itemEntities;
    }
}
