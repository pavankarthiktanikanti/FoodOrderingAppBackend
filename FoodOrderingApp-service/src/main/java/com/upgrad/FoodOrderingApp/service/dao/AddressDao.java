package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the Address to Database
     *
     * @param address The Customer address
     * @return The Persisted Address
     */
    public AddressEntity saveAddress(AddressEntity address) {
        entityManager.persist(address);
        return address;
    }

    /**
     * checks if state uuid is present or not
     *
     * @param stateUuid The state uuid that needs to be verified
     * @return The state details if the matching uuid is present in state table
     */
    public StateEntity getStateByStateUUID(String stateUuid) {
        try {
            return entityManager.createNamedQuery("stateByStateUuid", StateEntity.class)
                    .setParameter("uuid", stateUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This will save the customer id and address id in the customer address table
     *
     * @param customerAddressEntity The customer id and address id that needs to be saved
     * @return customer address entity that is saved in db
     */
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    /**
     * This will return the saved addresses in descending order of their saved time
     *
     * @return list of Address Entity
     */
    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {
        return entityManager.createNamedQuery("allAddressesForCustomer", AddressEntity.class).setParameter("customerId", customerEntity.getId()).getResultList();
    }

    /**
     * This method is used to get the Customer Address Entity matching the address Uuid passed in request
     *
     * @param addressId address Uuid that is passed in request
     * @return Address Entity that matches with the address Uuid passed in request
     */
    public AddressEntity getAddressByUUID(String addressId) {
        try {
            return entityManager.createNamedQuery("addressByUuid", AddressEntity.class).setParameter("addressUUID", addressId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    /**
     * This method is used to get the Customer Address Entity matching the address Uuid passed in request
     *
     * @param addressId address Uuid that is passed in request
     * @return Customer Address Entity that is matching with the address Uuid passed in request
     */
    public CustomerAddressEntity getCustomerAddressByAddressUUID(String addressId) {
        try {
            return entityManager.createNamedQuery("customerAddressByAddressUuid", CustomerAddressEntity.class).setParameter("addressUUID", addressId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method is used to delete Address Entity fom database
     *
     * @param addressEntity Address Entity that needed to be deleted
     * @return Address Entity which is deleted
     */
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        entityManager.remove(addressEntity);
        return addressEntity;
    }

    /**
     * This will search for all the states in the data base
     *
     * @return List of State Entity
     */
    public List<StateEntity> getAllStates() {
        return entityManager.createNamedQuery("allStates", StateEntity.class).getResultList();
    }
}
