package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
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
     * @throws SignUpRestrictedException - The endpoint will throw this exception if any criteria doesn't match with the given endpoint definition.
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

        if (customerEntity.getFirstname() == null || customerEntity.getEmail() == null || customerEntity.getContactNumber() == null
                || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

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

        String[] splitAuthorization = authorization.split("\\s");

        String authCheck = "Basic" + " " + splitAuthorization[1];
        if(!authorization.equals(authCheck)) {
            throw new AuthenticationFailedException("ATH-003" , "Incorrect format of decoded customer name and password");
        }

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

    /**
     * A controller method for customer logout.
     * @param authorization - This argument requests the access-token of already logged-in customer.
     * @return ResponseEntity<LogoutResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException - The endpoint will throw this exception when details entered are incorrect or customer already logged-out or session expired.
     */


    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST , path = "/logout" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        String auth = "Bearer" + " " + authorization;
        CustomerAuthEntity customerAuthEntity = customerBusinessService.logout(auth);

        LogoutResponse logoutResponse = new LogoutResponse().id(customerAuthEntity.getCustomerId().getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);


    }

    /**
     * A controller method to update user details(Firstname , Lastname)
     * @param authorization -This argument requests the access-token of already logged-in customer.
     * @param updateCustomerRequest - This argument contains all the attributes required to update user details in the database.
     * @return ResponseEntity<UpdateCustomerResponse> type along with Http status OK.
     * @throws AuthorizationFailedException - The endpoint will throw this exception when details entered are incorrect or customer already logged-out or session expired.
     * @throws UpdateCustomerException - This endpoint will throws this exception when any field is empty or Password is weak/invalid.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT , path = "/" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader("authorization") final String authorization , UpdateCustomerRequest updateCustomerRequest) throws AuthorizationFailedException, UpdateCustomerException {

        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setFirstname(updateCustomerRequest.getFirstName());
        customerEntity.setLastname(updateCustomerRequest.getLastName());

        CustomerEntity updatedCustomerEntity = customerBusinessService.updateCustomer(authorization , customerEntity);

        UpdateCustomerResponse updateResponse = new UpdateCustomerResponse().id(updatedCustomerEntity.getUuid()).firstName(updatedCustomerEntity.getFirstname()).lastName(updatedCustomerEntity.getLastname()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateResponse, HttpStatus.OK);

    }

    /**
     *
     * @param authorization - This argument requests the access-token of already logged-in customer.
     * @param updatePasswordRequest - This argument requests all the attributes required to update password in database.
     * @return ResponseEntity<UpdatePasswordResponse> type along with Http status OK.
     * @throws AuthorizationFailedException - The endpoint will throw this exception when details entered are incorrect or customer already logged-out or session expired.
     * @throws UpdateCustomerException - The endpoint will throw this exception if any criteria doesn't match with the given endpoint definition.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT , path = "/password" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> changePassword(@RequestHeader("authorization") final String authorization , UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException , UpdateCustomerException {

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setPassword(updatePasswordRequest.getNewPassword());

        String oldPassword = updatePasswordRequest.getOldPassword();

        CustomerEntity updatePassword = customerBusinessService.updateCustomerPassword(authorization , customerEntity , oldPassword);

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(updatePassword.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse , HttpStatus.OK);

    }


}