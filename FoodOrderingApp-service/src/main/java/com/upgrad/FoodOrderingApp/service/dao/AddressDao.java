package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
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
     * @return
     */
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    /**
     * This will search for all the states in the db
     *
     * @return List of State Entity
     */
    public List<StateEntity> getAllStates() {
        return entityManager.createNamedQuery("allStates", StateEntity.class).getResultList();
    }
}
