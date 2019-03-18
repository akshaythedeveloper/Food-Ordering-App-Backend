package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressBusinessService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST , path = "/address" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(final SaveAddressRequest saveAddressRequest , @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {



        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setFlatBuilNumber(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setPincode(saveAddressRequest.getPincode());

        StateEntity stateEntity = new StateEntity();
        stateEntity.setUuid(saveAddressRequest.getStateUuid());

        AddressEntity createCustomerAddress = addressBusinessService.saveAddress(addressEntity , stateEntity,  authorization);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(createCustomerAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse , HttpStatus.CREATED);

    }
}
