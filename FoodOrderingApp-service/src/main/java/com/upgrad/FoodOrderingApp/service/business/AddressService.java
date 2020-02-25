package com.upgrad.FoodOrderingApp.service.business;


import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    /**
     * Saved  Customer address in Database
     * Validate the Pincode Format and the state uuid with that of state uuid in state table
     *
     * @param address  The details of address to be saved in Database
     * @param customer The details of customer who has logged in
     * @return Address Entity which is saved in data base
     * @throws SaveAddressException when pin code is invalid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity address, CustomerEntity customer) throws SaveAddressException {
        // Check if the pincode entered is valid or not
        if (FoodOrderingUtil.isInvalidPinCode(address.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        address.setUuid(UUID.randomUUID().toString());
        address.setActive(1);
        AddressEntity updatedAddress = addressDao.saveAddress(address);

        return address;
    }

    /**
     * This method will check if there is any entry in the state table with same stateUUID
     *
     * @param stateUUID state UUID that is passed through Save Address Reques
     * @return the State Enitity having the same state uuid as passed as parameter
     * @throws AddressNotFoundException will be thrown when no state is found
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
     * @return Customer Address Entity that is saved in data base
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        return addressDao.saveCustomerAddress(customerAddressEntity);
    }

    /**
     * This will return the saved addresses in descending order of their saved time
     *
     * @return list of Address Entity
     */
    public List<AddressEntity> getAllAddress(CustomerEntity customer) {
        return addressDao.getAllAddress(customer);
    }


    /**
     * This method is used to get the Address Entity from data base which needed to be deleted
     *
     * @param addressUuid    The address Uuid passed as request that needs to be deleted
     * @param loggedCustomer Customer Entity who has logged in
     * @return Address Entity that needed to be deleted
     * @throws AddressNotFoundException     If no address is present in db matching address Uuid passed as request
     * @throws AuthorizationFailedException If customer is not the one who has created the address
     */
    public AddressEntity getAddressByUUID(String addressUuid, CustomerEntity loggedCustomer) throws AddressNotFoundException, AuthorizationFailedException {
        AddressEntity address = addressDao.getAddressByUUID(addressUuid);
        if (address == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        } else {
            CustomerEntity customerForAddress = addressDao.getCustomerForAddress(addressUuid);
            //if the customer who has logged in is not same as the customer which belongs to the address to be deleted
            if (customerForAddress != null && customerForAddress.getId() == loggedCustomer.getId()) {
                return address;
            } else {
                throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");

            }
        }
    }

    /**
     * This method is used to delete the address from data base
     *
     * @param addressEntity The Address Entity that needed to be deleted
     * @return Address Entity that has been deleted
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        AddressEntity deletedAddress = addressDao.deleteAddress(addressEntity);
        return deletedAddress;
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
