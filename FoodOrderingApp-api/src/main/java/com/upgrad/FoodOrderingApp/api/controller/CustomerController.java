package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
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
     * @param authorization The Bearer authorization token from he headers
     * @return The uuid of the Customer after updating the logout at in records
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        CustomerAuthEntity customerAuth = customerService.logout(decodeBearerToken(authorization));
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.id(customerAuth.getCustomer().getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
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

    /**
     * Decode the Bearer Authorization Token
     *
     * @param authorization The Bearer authorization Token from the headers
     * @return The decoded access Token
     * @throws AuthorizationFailedException If the authorization token is not in valid format (missing Bearer prefix)
     *                                      throw an error message as not logged in
     */
    public String decodeBearerToken(String authorization) throws AuthorizationFailedException {
        try {
            String[] bearerToken = authorization.split(FoodOrderingUtil.BEARER_TOKEN);
            if (bearerToken != null && bearerToken.length > 1) {
                String accessToken = bearerToken[1];
                return accessToken;
            } else {
                throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
    }
}