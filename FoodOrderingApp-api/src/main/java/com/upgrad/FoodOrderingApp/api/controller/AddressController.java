package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.StatesList;
import com.upgrad.FoodOrderingApp.api.model.StatesListResponse;
import com.upgrad.FoodOrderingApp.service.business.AddressService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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