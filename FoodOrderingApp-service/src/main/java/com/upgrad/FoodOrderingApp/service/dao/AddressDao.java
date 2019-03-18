package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AddressEntity saveAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public CustomerAddressEntity createCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        entityManager.remove(addressEntity);
        return addressEntity;
    }


    public StateEntity getStateByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("stateByUuid" , StateEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public AddressEntity getAddressByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("addressByUuid" , AddressEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre){
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

    public TypedQuery<StateEntity> getAllStates() {
        try {
            return entityManager.createNamedQuery("allStates" , StateEntity.class);
        } catch (NullPointerException nre) {
            return null;
        }
    }

    public CustomerAddressEntity getCustomerAddress(CustomerEntity customerId) {
        try {
            return entityManager.createNamedQuery("getCustomerAddress" , CustomerAddressEntity.class).setParameter("customerId" , customerId).getSingleResult();
        } catch (NullPointerException nre) {
            return null;
        }
    }

    public TypedQuery<AddressEntity> getAllSavedAddresses() {
        try {
            return entityManager.createNamedQuery("allSavedAddresses" , AddressEntity.class);
        } catch (NullPointerException nre) {
            return null;
        }
    }

    public TypedQuery<CustomerAddressEntity> allCustomerAddress() {
        try {
            return entityManager.createNamedQuery("getAllCustomerAddress" , CustomerAddressEntity.class);
        } catch (NullPointerException nre) {
            return null;
        }
    }

    public StateEntity getStatesByAddress(String uuid) {
        try {
            return entityManager.createNamedQuery("stateByUuid" , StateEntity.class).setParameter("uuid" , uuid).getSingleResult();
        } catch (NullPointerException nre) {
            return null;
        }
    }
}
