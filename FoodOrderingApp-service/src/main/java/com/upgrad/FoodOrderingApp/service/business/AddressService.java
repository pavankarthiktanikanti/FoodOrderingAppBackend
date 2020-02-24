package com.upgrad.FoodOrderingApp.service.business;


import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    /**
     * Saved  Customer address in Database
     * Validate the Pincode Format and the state uuid with that of state uuid in state table
     *
     * @param address The details of address to be saved in Database
     * @return The saved Address entity Object
     * @throws SaveAddressException when pin code is invalid,
     * @throws AddressNotFoundException when no state found in state having same uuid
     */
    /**
     * @param address
     * @param customer
     * @return
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity address, CustomerEntity customer) throws SaveAddressException, AddressNotFoundException {
        // Check if the pincode entered is valid or not
        if (FoodOrderingUtil.isInvalidPinCode(address.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        address.setActive(1);
        AddressEntity updatedAddress = addressDao.saveAddress(address);

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customer);
        customerAddressEntity.setAddress(updatedAddress);

        return address;
    }

    /**
     * This will check if there is any entry in the state table with same stateUUID
     *
     * @param stateUUID state UUID that is passed through Save Address Request
     * @return State Entity matched with the uuid
     */
    public StateEntity getStateByUUID(String stateUUID) throws AddressNotFoundException {
        StateEntity state = addressDao.getStateByStateUUID(stateUUID);
        if (state == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return state;
    }

    /**
     * This will save the entry in the customer address table
     *
     * @param customerAddressEntity will be the customer id and address id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        return addressDao.saveCustomerAddress(customerAddressEntity);
    }

    /**
     * This will return all the states
     *
     * @return List of all states
     */
    public List<StateEntity> getAllStates() {
        return addressDao.getAllStates();
    }
}
