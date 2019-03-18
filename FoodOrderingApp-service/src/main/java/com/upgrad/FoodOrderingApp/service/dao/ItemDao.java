package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {
    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemById(String itemId) {
        try {
            return entityManager.createNamedQuery("getItemById", ItemEntity.class).setParameter("uuid", itemId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }



    public List<OrderItemEntity> getItemByOrder(OrdersEntity ordersEntity,ItemEntity itemEntity) {
        try {
            return entityManager.createNamedQuery("getItemByOrderAndRestaurant", OrderItemEntity.class).setParameter("orderId", ordersEntity).
                    setParameter("itemId",itemEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
