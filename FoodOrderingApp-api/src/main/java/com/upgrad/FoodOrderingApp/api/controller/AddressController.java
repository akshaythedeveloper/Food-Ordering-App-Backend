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
import sun.jvm.hotspot.debugger.Address;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressBusinessService;

    /**
     * A coltroller method to save address of a customer in the database.
     * @param saveAddressRequest - This argument contains all the attributes required to store customer address details in the database.
     * @param authorization - This argument requests the access-token of already logged-in customer in @RequestHeader
     * @return ResponseEntity<SaveAddressResponse>
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
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

    /**
     * A controller method to getAllSaved address of a specific customer.
     * @param authorization - This argument requests the access-token of already logged-in customer in @RequestHeader
     * @return ResponseEntity<List<AddressListResponse>> with Http status OK.
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET , path = "/address/customer" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<List<AddressListResponse>> getAllSavedAddresses(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<AddressEntity> addressEntityList = addressBusinessService.getAllSavedAddresses(authorization);

       // List<StateEntity> statesEntityList = addressBusinessService.getAllStates().getResultList();


        List<AddressListResponse> addressListResponses = new ArrayList<>();

        List<AddressList> addressLists = new ArrayList<>();
        List<StatesList> statesLists = new ArrayList<>();


        for(AddressEntity addressEntity : addressEntityList) {

            AddressList addressList = new AddressList();

            addressList.setId(UUID.fromString(addressEntity.getUuid()));
            addressList.setFlatBuildingName(addressEntity.getFlatBuilNumber());
            addressEntity.setLocality(addressEntity.getLocality());
            addressEntity.setCity(addressEntity.getCity());
            addressEntity.setPincode(addressEntity.getPincode());
            StateEntity stateEntity = addressBusinessService.getStatesByAddress(addressEntity.getStateId().getUuid());

            addressEntity.setStateId(stateEntity);
            addressLists.add(addressList);

        }

        //AddressListResponse addressListResponse = new AddressListResponse().addresses(addressEntityList).addAddressesItem()

        addressListResponses.add(new AddressListResponse().addresses(addressLists));
        return new ResponseEntity<List<AddressListResponse>>(addressListResponses, HttpStatus.OK);

    }


    /**
     * A controller method to Delete saved address of a specific customer.
     * @param address_id - UUID of the customer to be deleted from Address Tables.
     * @param authorization - This argument requests the access-token of already logged-in customer in @RequestHeader
     * @return ResponseEntity<List<StatesListResponse>> with Http Status OK
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
   @RequestMapping(method = RequestMethod.DELETE , path = "/address/{address_id}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@PathVariable("address_id") final String address_id , @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException , AddressNotFoundException {

       AddressEntity deleteAddressEntity = addressBusinessService.deleteAddress(address_id , authorization);

       DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deleteAddressEntity.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");
       return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse , HttpStatus.OK);
   }


    /**
     * A controller method to get all states from States table.
     * @return ResponseEntity<List<StatesListResponse>> with Http status OK
     */
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

        statesListResponses.add(new StatesListResponse().states(statesLists));
        return new ResponseEntity<List<StatesListResponse>>(statesListResponses, HttpStatus.OK);


    }




}
