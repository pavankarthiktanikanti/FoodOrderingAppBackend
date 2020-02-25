package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.AddressService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class AddressController {


    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    /**
     * Saves the address after validating the Bearer authorization
     * token with the Database records
     * Throw error message when the access token is invalid/expired/not present in Database
     * Checks whether all the fields are present in address Request
     *
     * @param authorization      The Bearer authorization token from the headers
     * @param saveAddressRequest The request which contain all the field of the address to be saved
     * @return The uuid of the address after saving it
     * @throws SaveAddressException         If any of field in SaveAddressRequest is empty or null
     * @throws AddressNotFoundException     If the state uuid  is not present in state table
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,
                                                           @RequestBody(required = false) SaveAddressRequest saveAddressRequest)
            throws SaveAddressException, AddressNotFoundException, AuthorizationFailedException {

        CustomerEntity customer = customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));
        // Check if any of the fields are not set, if so throw Error message
        if (FoodOrderingUtil.isInValid(saveAddressRequest.getFlatBuildingName()) || FoodOrderingUtil.isInValid(saveAddressRequest.getLocality())
                || FoodOrderingUtil.isInValid(saveAddressRequest.getCity()) || FoodOrderingUtil.isInValid(saveAddressRequest.getPincode()) ||
                FoodOrderingUtil.isInValid(saveAddressRequest.getStateUuid())) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        // Check if the state id passed is present State table or not
        StateEntity state = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        final AddressEntity address = new AddressEntity();

        address.setState(state);
        address.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        address.setLocality(saveAddressRequest.getLocality());
        address.setCity(saveAddressRequest.getCity());
        address.setPincode(saveAddressRequest.getPincode());
        AddressEntity updatedAddress = addressService.saveAddress(address, customer);

        //save the address in the customer address table
        final CustomerAddressEntity customerAddress = new CustomerAddressEntity();
        customerAddress.setCustomer(customer);
        customerAddress.setAddress(address);
        addressService.saveCustomerAddress(customerAddress);

        SaveAddressResponse response = new SaveAddressResponse();
        response.id(updatedAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(response, HttpStatus.CREATED);
    }

    /**
     * This method is used to get All the saved addresses in descending order of their saved time
     * for a signed in user
     *
     * @param authorization The Bearer authorization token from the headers
     * @return Address List of all the saved the addresses in the db
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/address/customer")
    public ResponseEntity<AddressListResponse> getAllAddress(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        CustomerEntity customer = customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));
        List<AddressEntity> addresss = addressService.getAllAddress(customer);
        List<AddressList> addressLists = new ArrayList<>();
        //Check if any address is returned or not
        if (addresss != null && !addresss.isEmpty()) {
            for (AddressEntity address : addresss) {
                AddressList addressList = new AddressList();
                AddressListState addressListState = new AddressListState();
                addressListState.id(UUID.fromString(address.getState().getUuid())).stateName(address.getState().getStateName());
                addressList.id(UUID.fromString(address.getUuid())).flatBuildingName(address.getFlatBuilNo()).
                        locality(address.getLocality()).city(address.getCity()).pincode(address.getPincode()).state(addressListState);
                addressLists.add(addressList);

            }
        }
        AddressListResponse addressListResponse = new AddressListResponse();
        addressListResponse.setAddresses(addressLists);
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     * This is used to get the list of all states
     * No authorization required for this endpoint
     *
     * @return List of all States available in db
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/states")
    public ResponseEntity<StatesListResponse> getAllStates() {
        List<StateEntity> states = addressService.getAllStates();
        List<StatesList> statesLists = new ArrayList<>();
        //Check if any state is returned or not
        if (states != null && !states.isEmpty()) {
            for (StateEntity state : states) {
                StatesList stateList = new StatesList();
                stateList.id(UUID.fromString(state.getUuid())).stateName(state.getStateName());
                statesLists.add(stateList);
            }
        }
        StatesListResponse statesListResponse = new StatesListResponse();
        statesListResponse.setStates(statesLists);
        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
    }
}