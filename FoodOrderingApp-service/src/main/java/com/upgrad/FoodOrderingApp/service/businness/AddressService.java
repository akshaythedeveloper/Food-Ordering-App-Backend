package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity , StateEntity stateEntity , String authorization) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException{

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerByAccessToken(authorization);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        ZonedDateTime expireTime = customerAuthEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();

        if (expireTime.isBefore(currentTime)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }


        ZonedDateTime logoutAtTime = customerAuthEntity.getLogoutAt();
        if (logoutAtTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");

        }

        if(addressEntity.getFlatBuilNumber() == null || addressEntity.getCity() == null || addressEntity.getLocality() == null || addressEntity.getPincode() == null) {
            throw new SaveAddressException("SAR-001" , "No field can be empty");
        }

        String pincodeValidation = "[0-9]{6}";
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile(pincodeValidation);
        Matcher matcher1 = pattern1.matcher(addressEntity.getPincode());
        if (!matcher1.matches()) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        StateEntity stateEntity1 = addressDao.getStateByUuid(stateEntity.getUuid());

        if(stateEntity1 == null) {
            throw new AddressNotFoundException("ANF-002" , "No state by this id");
        }

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomerId(customerAuthEntity.getCustomerId());
        customerAddressEntity.setAddressId(addressEntity);

        addressEntity.setStateId(stateEntity1);
        addressEntity.setActive(1);

        addressDao.saveAddress(addressEntity);
        addressDao.createCustomerAddress(customerAddressEntity);


        return addressEntity;

    }

    public List<AddressEntity> getAllSavedAddresses(String authorization) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthEntity = addressDao.getCustomerByAccessToken(authorization);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        ZonedDateTime expireTime = customerAuthEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();

        if (expireTime.isBefore(currentTime)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }


        ZonedDateTime logoutAtTime = customerAuthEntity.getLogoutAt();
        if (logoutAtTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }


        CustomerEntity customerEntity = customerAuthEntity.getCustomerId();

        List<AddressEntity> customerAddressEntityList = new ArrayList<>();

        List<CustomerAddressEntity> customerAddressEntity = addressDao.getCustomerAddress(customerEntity);

        for(CustomerAddressEntity customerAddressEntity1 : customerAddressEntity) {
            customerAddressEntityList.add(customerAddressEntity1.getAddressId());

        }

        return customerAddressEntityList;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(String addressId , String authorization) throws AuthorizationFailedException , AddressNotFoundException {

        CustomerAuthEntity customerAuthEntity = addressDao.getCustomerByAccessToken(authorization);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        ZonedDateTime expireTime = customerAuthEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();

        if (expireTime.isBefore(currentTime)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        ZonedDateTime logoutAtTime = customerAuthEntity.getLogoutAt();
        if (logoutAtTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if(addressId == null) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        AddressEntity addressEntity = addressDao.getAddressByUuid(addressId);

        if(addressEntity == null) {
            throw new AddressNotFoundException("ANF-003" , "No address by this id");
        }

        CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddress(customerAuthEntity.getCustomerId());

        if(customerAuthEntity.getCustomerId() == customerAddressEntity.getCustomerId()) {
            return addressDao.deleteAddress(addressEntity);
        } else {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

    }

    public TypedQuery<StateEntity> getAllStates() {

        return addressDao.getAllStates();

    }


    public StateEntity getStatesByAddress(String uuid) {
        StateEntity stateEntity = addressDao.getStatesByAddress(uuid);
        return stateEntity;
    }
}
