package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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

        // Generate encrypted password and store the Customer details in Database
        String password = customer.getPassword();
        String[] encryptedText = cryptographyProvider.encrypt(password);
        customer.setSalt(encryptedText[0]);
        customer.setPassword(encryptedText[1]);
        customerDao.saveCustomer(customer);
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
}
