package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@CrossOrigin
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * This method registers a customer with all the details provided and handles the
     * Scenario when customer provides empty values for any field other than last name
     * and throws an error message
     *
     * @param customerRequest Holds all the details keyed in by the customer at the time of Sign up
     * @return UUID of the registered customer for further login
     * @throws SignUpRestrictedException if the user provides invalid values for fields other than last name
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody final SignupCustomerRequest customerRequest)
            throws SignUpRestrictedException {
        // Check if any of the fields are not set, if so throw Error message
        if (FoodOrderingUtil.isInValid(customerRequest.getFirstName())
                || FoodOrderingUtil.isInValid(customerRequest.getEmailAddress())
                || FoodOrderingUtil.isInValid(customerRequest.getContactNumber())
                || FoodOrderingUtil.isInValid(customerRequest.getPassword())) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        final CustomerEntity customer = new CustomerEntity();
        customer.setUuid(UUID.randomUUID().toString());
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        customer.setEmail(customerRequest.getEmailAddress());
        customer.setContactNumber(customerRequest.getContactNumber());
        customer.setPassword(customerRequest.getPassword());
        final CustomerEntity createdCustomer = customerService.saveCustomer(customer);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse();
        signupCustomerResponse.setId(createdCustomer.getUuid());
        signupCustomerResponse.setStatus("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }
}