package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
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

import java.util.ArrayList;
import java.util.List;

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

   /** @RequestMapping(method = RequestMethod.GET , path = "/address/customer" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<List<AddressListResponse>> getAllSavedAddresses(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<AddressEntity> addressEntityList = addressBusinessService.getAllSavedAddresses(authorization).getResultList();

        List<AddressListResponse> statesListResponses = new ArrayList<>();

        List<AddressList>

        //AddressListResponse addressListResponse = new AddressListResponse().addresses(addressEntityList).addAddressesItem()

        return new ResponseEntity<>(addressEntityList, HttpStatus.OK);

    }**/


   @RequestMapping(method = RequestMethod.DELETE , path = "/address/{address_id}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@PathVariable("address_id") final String address_id , @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException , AddressNotFoundException {

       AddressEntity deleteAddressEntity = addressBusinessService.deleteAddress(address_id , authorization);

       DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deleteAddressEntity.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");
       return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse , HttpStatus.OK);
   }


    @RequestMapping(method = RequestMethod.GET , path = "/states" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<StatesListResponse>> getAllStates() {

        List<StateEntity> statesEntityList = addressBusinessService.getAllStates().getResultList();

        List<StatesListResponse> statesListResponses = new ArrayList<>();

        List<StatesList> statesLists = new ArrayList<>();


        for(StateEntity stateEntity : statesEntityList) {

            StatesList statesList = new StatesList();

            statesList.setId(UUID.fromString(stateEntity.getUuid()));
            statesList.setStateName(stateEntity.getStateName());
            statesLists.add(statesList);

        }

        //StatesListResponse statesListResponse = new StatesListResponse().states(statesLists);
        statesListResponses.add(new StatesListResponse().states(statesLists));
        return new ResponseEntity<List<StatesListResponse>>(statesListResponses, HttpStatus.OK);


    }




}
