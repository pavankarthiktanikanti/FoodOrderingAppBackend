package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
}
