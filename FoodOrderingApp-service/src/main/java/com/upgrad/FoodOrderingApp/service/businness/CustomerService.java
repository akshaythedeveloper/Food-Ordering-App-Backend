package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {


        CustomerEntity customerEntity1 = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());
        if (customerEntity1 != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        String emailValidation = "^(.+)@(.+)$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailValidation);
        Matcher matcher = pattern.matcher(customerEntity.getEmail());
        if (!matcher.matches()) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        String contactValidation = "[0-9]{10}";
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile(contactValidation);
        Matcher matcher1 = pattern1.matcher(customerEntity.getContactNumber());

        if (!matcher1.matches()) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }


        String passwordStrengthValidation = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^])(?=\\S+$).{8,}$";
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile(passwordStrengthValidation);
        Matcher matcher2 = pattern2.matcher(customerEntity.getPassword());
        if (!matcher2.matches()) {
            throw new SignUpRestrictedException("SGR-004", "Weak Password");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        return customerDao.createCustomer(customerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {

        String userNameValidation = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(userNameValidation);
        Matcher matcher = pattern.matcher(username);

        String passwordValidation = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^])(?=\\S+$).{8,}$";
        Pattern pattern1 = Pattern.compile(passwordValidation);
        Matcher matcher1 = pattern1.matcher(password);




        if (!matcher.matches() || !matcher1.matches()) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        CustomerEntity customerEntity = customerDao.getUserByEmail(username);

        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());

        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            customerAuthEntity.setCustomerId(customerEntity);

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setLoginAt(ZonedDateTime.now());
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));


            return customerDao.createCustomerAuth(customerAuthEntity);
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(String authorization) throws AuthorizationFailedException {

        String[] accessToken = authorization.split("\\s");

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerByAccessToken(accessToken[1]);

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

        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerDao.updateCustomerAuth(customerAuthEntity);

        return customerAuthEntity;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(String authorization, CustomerEntity customerEntity) throws AuthorizationFailedException, UpdateCustomerException {

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

        if (customerEntity.getFirstname() == null) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        CustomerEntity customerEntity1 = customerAuthEntity.getCustomerId();
        customerEntity1.setFirstname(customerEntity.getFirstname());
        customerEntity1.setLastname(customerEntity.getLastname());

        return customerDao.updateCustomer(customerEntity1);

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String authorization , CustomerEntity customerEntity , String oldPassword) throws AuthorizationFailedException , UpdateCustomerException {

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

        if(oldPassword == null || customerEntity.getPassword() == null) {
            throw new UpdateCustomerException("UCR-003" , "No field Should be empty");
        }

        String passwordValidation = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordValidation);
        Matcher matcher = pattern.matcher(customerEntity.getPassword());

        if (!matcher.matches()) {
            throw new UpdateCustomerException("UCR-001", "Weak Password");
        }

        CustomerEntity customerEntity1 = customerAuthEntity.getCustomerId();

        String encryptedText = passwordCryptographyProvider.encrypt(oldPassword , customerEntity1.getSalt());

        if(!encryptedText.equals(customerEntity1.getPassword())) {
            throw new UpdateCustomerException("UCR-004" , "IncorrectOld Password!");

        }

        String[] encrypted = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity1.setSalt(encrypted[0]);
        customerEntity1.setPassword(encrypted[1]);

        return customerDao.changePassword(customerEntity1);

    }
}