package com.upgrad.FoodOrderingApp.service.dao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

/**
 * CustomerDao class provides the database access for all the endpoints in  CustomerController.
 */
@Repository
public class CustomerDao {

    //When a container of the application(be it a Java EE container or any other custom container like Spring) manages the lifecycle of the Entity Manager, the Entity Manager is said to be Container Managed. The most common way of acquiring a Container Managed EntityManager is to use @PersistenceContext annotation on an EntityManager attribute.
    @PersistenceContext
    private EntityManager entityManager;



    public CustomerEntity getCustomerByContactNumber(String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber" , CustomerEntity.class).setParameter("contactNumber" , contactNumber).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity getUserByEmail(String username) {
        try {
            return entityManager.createNamedQuery("customerByEmail" , CustomerEntity.class).setParameter("email" , username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity getCustomerByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("customerByAccessToken" , CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public CustomerEntity getCustomerById(CustomerAuthEntity customerId) {
        try {
            return entityManager.createNamedQuery("customerByCustomerId" , CustomerEntity.class).setParameter("customerId" , customerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity changePassword(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
        return customerEntity;
    }

    public CustomerAuthEntity createCustomerAuth(CustomerAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public CustomerAuthEntity updateCustomerAuth(CustomerAuthEntity customerAuthEntity) {
        return entityManager.merge(customerAuthEntity);
    }

    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        return entityManager.merge(customerEntity);
    }


    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }
}
