package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the Customer to Database
     *
     * @param customer The Customer data who Signed up
     * @return The Persisted Customer with id generated
     */
    public CustomerEntity saveCustomer(CustomerEntity customer) {
        entityManager.persist(customer);
        return customer;
    }

    /**
     * Retrieve the Customer by Contact Number
     *
     * @param contactNumber of the Customer to be retrieved
     * @return The Customer Entity if a matched Contact Number, else null
     */
    public CustomerEntity getCustomerByContact(String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Saves the Customer Auth along with access token when ever logged in
     *
     * @param customerAuth The Auth entity which has generated access token
     * @return The persisted Customer Auth Entity with id generated
     */
    public CustomerAuthEntity createAuthToken(CustomerAuthEntity customerAuth) {
        entityManager.persist(customerAuth);
        return customerAuth;
    }
}