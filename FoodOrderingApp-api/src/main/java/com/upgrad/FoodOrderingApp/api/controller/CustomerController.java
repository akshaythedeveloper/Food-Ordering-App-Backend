package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerBusinessService;

    /**
     * A controller method for user signup.
     * @param signupCustomerRequest - This argument contains all the attributes required to store user details in the database.
     * @return - ResponseEntity<SignupUserResponse> type object along with Http status CREATED.
     * @throws SignUpRestrictedException
     */

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST , path = "/signup" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        final CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstname(signupCustomerRequest.getFirstName());
        customerEntity.setLastname(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity createdCustomerEntity = customerBusinessService.saveCustomer(customerEntity);

        SignupCustomerResponse customerResponse =  new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);

    }

    /**
     * A controller method for customer login.
     * @param authorization - This argument requests the authorization in Base64 encoded format. (username:password)
     * @return ResponseEntity<LoginResponse> type object along with Http status CREATED.
     * @throws AuthenticationFailedException
     */


    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST , path = "/login" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        CustomerAuthEntity createdCustomerAuthEntity = customerBusinessService.authenticate(decodedArray[0], decodedArray[1]);
        CustomerEntity customer = createdCustomerAuthEntity.getCustomerId();

        LoginResponse loginResponse = new LoginResponse().id(customer.getUuid()).firstName(customer.getFirstname()).lastName(customer.getLastname())
                .emailAddress(customer.getEmail()).contactNumber(customer.getContactNumber()).message("LOGGED IN SUCCESSFULLY");

        List<String> header = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", createdCustomerAuthEntity.getAccessToken());
        headers.setAccessControlExposeHeaders(header);
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);

    }


    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST , path = "/logout" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthEntity = customerBusinessService.logout(authorization);

        LogoutResponse logoutResponse = new LogoutResponse().id(customerAuthEntity.getCustomerId().getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);


    }
}