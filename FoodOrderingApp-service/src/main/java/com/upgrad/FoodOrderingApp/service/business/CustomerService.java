package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * Saved the Signed up Customer details in Database
     * Validate the Email format, Contact Number format and Password Strength
     * If the contact number entered is already registered, throw error message
     *
     * @param customer The details of customer to be saved in Database
     * @return The saved Customer entity Object with the id populated
     * @throws SignUpRestrictedException when email/Contact number is invalid,
     *                                   or weak password or contact number is already registered
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customer) throws SignUpRestrictedException {

        // Check if the email id format is not valid
        if (FoodOrderingUtil.isInValidEmail(customer.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        // Check if the contact number is 10 digits or not
        if (FoodOrderingUtil.isInValidContactNumber(customer.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        // Check for Strong Password format
        if (!FoodOrderingUtil.isStrongPassword(customer.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        // Check if the same contact number is already registered
        if (customerDao.getCustomerByContact(customer.getContactNumber()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        // Generate random uuid
        customer.setUuid(UUID.randomUUID().toString());
        // Generate encrypted password and store the Customer details in Database
        String password = customer.getPassword();
        String[] encryptedText = cryptographyProvider.encrypt(password);
        customer.setSalt(encryptedText[0]);
        customer.setPassword(encryptedText[1]);
        customer = customerDao.saveCustomer(customer);
        return customer;
    }

    /**
     * Authenticates the Customer when ever logged in with the contact number and password
     * Validates if the contact number is registered or not, if not registered, throws error message
     * Validate the password whether matching with the encrypted password stored in database
     * If Credentials are valid, then generate jwt access token and store in the database
     *
     * @param contactNumber The login Contact number of the Customer
     * @param password      The Password for signing in
     * @return The Customer Auth with the generated jwt access token
     * @throws AuthenticationFailedException when credentials are not valid/mismatched with the Database records
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {
        // Check if contact number/password is null/empty
        if (FoodOrderingUtil.isInValid(contactNumber) || FoodOrderingUtil.isInValid(password)) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        CustomerEntity customer = customerDao.getCustomerByContact(contactNumber);

        // If no record is found with the given Contact Number, throw error message
        if (customer == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, customer.getSalt());
        // Check if the password matches with the encrypted password stored in Database
        if (encryptedPassword.equals(customer.getPassword())) {
            // Generate jwt token based on the password and store in Database
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuth = new CustomerAuthEntity();
            customerAuth.setCustomer(customer);
            customerAuth.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuth.setAccessToken(jwtTokenProvider.generateToken(customer.getUuid(), now, expiresAt));
            customerAuth.setLoginAt(now);
            customerAuth.setExpiresAt(expiresAt);
            return customerDao.createAuthToken(customerAuth);
        } else {
            // Throw Exception if the credentials doesn't match with the Database records
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

    }

    /**
     * Validate the Customer access token and update the logout time in Database if valid
     * Throw error message if the access token is not present in Database or invalid or expired
     *
     * @param accessToken The jwt access token of the Customer
     * @return The Customer Auth record for the matched Customer
     * @throws AuthorizationFailedException If the token is not present/invalid/expired
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuth = validateCustomerAuthorization(accessToken);
        final ZonedDateTime logoutAt = ZonedDateTime.now();
        customerAuth.setLogoutAt(logoutAt);
        customerDao.updateCustomerAuth(customerAuth);
        return customerAuth;
    }

    /**
     * Retrieve the Customer Record based on the access token after validating the token
     * Throw error message if the token is expired/invalid/not present in Database
     *
     * @param accessToken The jwt access token of the customer
     * @return The Customer entity retrieved from the Database based on the access token
     * @throws AuthorizationFailedException if the token is expired/invalid/not present in Database
     */
    public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuth = validateCustomerAuthorization(accessToken);
        return customerAuth.getCustomer();
    }

    /**
     * Update the Customer's first name or last name based on the customer request
     *
     * @param customerToUpdate The Customer entity to be updated to Database
     * @return The Updated Customer from Database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerToUpdate) {
        return customerDao.updateCustomer(customerToUpdate);
    }

    /**
     * Validate the Customer access token and throw error message if the access token is not present
     * in Database or invalid or expired
     * Validate the old password with the one in the Database and update the new encrypted password to Database
     * Validates if the new password is strong else throw error as Weak password
     * If old password doesn't match with the records throw error as incorrect old password
     *
     * @param oldPassword      The old password of the Customer
     * @param newPassword      The new password to be updated in the Database
     * @param customerToUpdate The Customer Entity to be updated with new password
     * @return The Customer Entity after updating the password
     * @throws UpdateCustomerException If the new password is not String and old password doesn't match
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customerToUpdate)
            throws UpdateCustomerException {
        if (!FoodOrderingUtil.isStrongPassword(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        final String encryptedPassword = cryptographyProvider.encrypt(oldPassword, customerToUpdate.getSalt());
        // Check if old encrypted password matches with Database records
        if (encryptedPassword.equals(customerToUpdate.getPassword())) {
            final String[] encryptedNewPassword = cryptographyProvider.encrypt(newPassword);
            customerToUpdate.setSalt(encryptedNewPassword[0]);
            customerToUpdate.setPassword(encryptedNewPassword[1]);
            return customerDao.updateCustomer(customerToUpdate);
        }
        // If Old Password Doesn't match the password in database
        throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
    }

    /**
     * Validate the access token if present in Database or not and if customer not logged out before
     * and Expiry of the token is still not reached
     * Error message is thrown based on the access token status and returns the Customer Auth Model after updating
     * the logout time in Database
     *
     * @param accessToken The jwt access token of the Customer
     * @return The Customer Auth with the logout time updated in Database
     * @throws AuthorizationFailedException If the token is not valid/not found in Database or expired
     */
    public CustomerAuthEntity validateCustomerAuthorization(String accessToken) throws AuthorizationFailedException {
        if (accessToken != null) {
            CustomerAuthEntity customerAuth = customerDao.getCustomerAuthByAccessToken(accessToken);
            // Token is not matched with the database records
            if (customerAuth == null) {
                throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
            }
            // Customer Already Logged out
            if (customerAuth.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            }

            // Validating Session Expiry is with in 8 hours or not
            if (!isUserSessionValid(customerAuth.getExpiresAt())) {
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
            }
            return customerAuth;
        } else {
            // If the access token is not a valid string to validate
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
    }

    /**
     * Validate the Session Expiry time if it is with in the limit of 8 hours and still in future time
     * compared to current time
     *
     * @param expiryTime The Expiry time of the token in the Database
     * @return true if the acess token expiry didn't reach compared with current time
     */
    public Boolean isUserSessionValid(ZonedDateTime expiryTime) {
        if (expiryTime != null) {
            Long timeDifference = ChronoUnit.MILLIS.between(ZonedDateTime.now(), expiryTime);
            // Negative timeDifference indicates an expired access token,
            // difference should be with in the limit, token will be expired after 8 hours
            return (timeDifference >= 0 && timeDifference <= FoodOrderingUtil.EIGHT_HOURS_IN_MILLIS);
        }
        // Token expired or customer never signed in before(may also be the case of invalid token)
        return false;
    }
}
