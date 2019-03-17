package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;

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

        if (customerEntity.getFirstname() == null || customerEntity.getEmail() == null || customerEntity.getContactNumber() == null
                || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
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
}