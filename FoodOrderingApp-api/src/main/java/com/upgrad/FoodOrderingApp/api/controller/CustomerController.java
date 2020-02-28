package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
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

    /**
     * Validates the Customer Credentials and logs in the Customer and returns the generated
     * jwt token for the customer to accesses the apis further
     *
     * @param authorization The Basic Authorization token having the Credentials
     * @return The Customer basic details along with the generated jwt token in header
     * @throws AuthenticationFailedException If the authorization isn't valid or the credentials doesn't match with records
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        String[] decodedText = decodeBasicAuthorization(authorization);
        // If the authorization header doesn't have valid format, throw error message
        if (decodedText == null || decodedText.length < 1) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        // Authenticate the customer and generate the jwt access token for further access to apis
        CustomerAuthEntity customerAuth = customerService.authenticate(decodedText[0], decodedText[1]);
        CustomerEntity customer = customerAuth.getCustomer();
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id(customer.getUuid()).firstName(customer.getFirstName()).lastName(customer.getLastName())
                .emailAddress(customer.getEmail()).contactNumber(customer.getContactNumber())
                .message("LOGGED IN SUCCESSFULLY");
        List<String> header = new ArrayList<>();
        // Set the generated access token in the response headers
        header.add("access-token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccessControlExposeHeaders(header);
        httpHeaders.add("access-token", customerAuth.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, httpHeaders, HttpStatus.OK);
    }

    /**
     * Logout the Customer after validating the access token
     * Updates the logout at in the Database records and returns the uuid of the Customer
     *
     * @param authorization The Bearer authorization token from the headers
     * @return The uuid of the Customer after updating the logout at in records
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        CustomerAuthEntity customerAuth = customerService.logout(FoodOrderingUtil.decodeBearerToken(authorization));
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.id(customerAuth.getCustomer().getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    /**
     * Updates the first name and last name provided by the customer after validating the Bearer authorization
     * token with the Database records.
     * Throw error message when the access token is invalid/expired/not present in Database
     *
     * @param authorization         The Bearer authorization token from the headers
     * @param updateCustomerRequest The request object which has the first name and last name to be updated
     * @return The uuid of the customer updated along with Success message
     * @throws UpdateCustomerException      If the passed First Name is not valid as it is mandatory field
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestHeader("authorization") final String authorization,
                                                                 @RequestBody UpdateCustomerRequest updateCustomerRequest)
            throws UpdateCustomerException, AuthorizationFailedException {
        if (FoodOrderingUtil.isInValid(updateCustomerRequest.getFirstName())) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        CustomerEntity customerToUpdate = customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));
        customerToUpdate.setFirstName(updateCustomerRequest.getFirstName());
        if (updateCustomerRequest.getLastName() != null && !updateCustomerRequest.getLastName().isEmpty()) {
            customerToUpdate.setLastName(updateCustomerRequest.getLastName());
        }
        CustomerEntity updatedCustomer = customerService.updateCustomer(customerToUpdate);
        UpdateCustomerResponse response = new UpdateCustomerResponse();
        response.id(updatedCustomer.getUuid()).firstName(updatedCustomer.getFirstName()).lastName(updatedCustomer.getLastName())
                .setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(response, HttpStatus.OK);
    }

    /**
     * Updates the Customer Password with the one provided by the customer after validating the Bearer authorization
     * token with the Database records.
     * Throw error message when the access token is invalid/expired/not present in Database
     * Checks whether the old password matches with the one in Database before updating the new password
     *
     * @param authorization         The Bearer authorization token from the headers
     * @param updatePasswordRequest The request object which has the old and new passwords
     * @return The uuid of the Customer after updating the password
     * @throws UpdateCustomerException      If the passed old/new password fields are empty or null
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(@RequestHeader("authorization") final String authorization,
                                                                         @RequestBody UpdatePasswordRequest updatePasswordRequest)
            throws UpdateCustomerException, AuthorizationFailedException {
        if (FoodOrderingUtil.isInValid(updatePasswordRequest.getOldPassword())
                || FoodOrderingUtil.isInValid(updatePasswordRequest.getNewPassword())) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        CustomerEntity customerToUpdate = customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));
        CustomerEntity updatedCustomer = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), customerToUpdate);
        UpdatePasswordResponse response = new UpdatePasswordResponse();
        response.id(updatedCustomer.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(response, HttpStatus.OK);
    }

    /**
     * Decode the Basic Authorization Token
     * Added this logic here, due to the constraint of Mocks for Service class in Test cases
     *
     * @param authorization The Basic Authorization Token from the Headers
     * @return The decoded array of contact number and password
     * @throws AuthenticationFailedException If the authorization token is not in correct format
     */
    private String[] decodeBasicAuthorization(final String authorization) throws AuthenticationFailedException {
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split(FoodOrderingUtil.BASIC_TOKEN)[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(FoodOrderingUtil.COLON);
            return decodedArray;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
    }
}