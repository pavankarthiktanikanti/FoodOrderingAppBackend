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
     * Save the Customer address in Database
     * Validate the pin code Format and the state uuid with that of state uuid in state table
     *
     * @param address  The details of address to be saved in Database
     * @param customer The details of customer who has logged in
     * @return Address Entity which is saved in data base
     * @throws SaveAddressException when input fields are empty or pin code is invalid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity address, CustomerEntity customer) throws SaveAddressException {

        // Check if any of the fields are not set, if so throw Error message
        if (FoodOrderingUtil.isInValid(address.getFlatBuilNo()) || FoodOrderingUtil.isInValid(address.getLocality())
                || FoodOrderingUtil.isInValid(address.getCity()) || FoodOrderingUtil.isInValid(address.getPincode()) ||
                address.getState() == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        // Check if the pin code entered is valid or not
        if (FoodOrderingUtil.isInvalidPinCode(address.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        address.setUuid(UUID.randomUUID().toString());
        // Address is active by default unless any order is placed
        address.setActive(1);
        AddressEntity updatedAddress = addressDao.saveAddress(address);

        return address;
    }

    /**
     * This method will check if there is any entry in the state table with same stateUUID
     *
     * @param stateUUID state UUID that is passed through Save Address Request
     * @return the State Entity matched with the state uuid passed as parameter
     * @throws AddressNotFoundException when no state is found with the uuid
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
     * @param customerAddressEntity will be having the customer id and address id
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
     * @return Address Entity that is deleted
     * @throws AddressNotFoundException     If no address is present in db matching address Uuid passed as request
     * @throws AuthorizationFailedException If customer is not the one who has created the address
     */
    public AddressEntity getAddressByUUID(String addressUuid, CustomerEntity loggedCustomer) throws AddressNotFoundException, AuthorizationFailedException {
        //checking if the address uuid is empty
        if (FoodOrderingUtil.isInValid(addressUuid)) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity address = addressDao.getAddressByUUID(addressUuid);
        // Check if any address matched with the uuid, otherwise throw error
        if (address == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddressByAddressUUID(addressUuid);
        //if the customer who has logged in is not same as the customer which belongs to the address to be deleted
        if (customerAddressEntity == null || (customerAddressEntity.getCustomer() != null
                && customerAddressEntity.getCustomer().getId() != loggedCustomer.getId())) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        return customerAddressEntity.getAddress();
    }

    /**
     * This method is used to delete the address from data base
     * If the value of variable active is 0 or null than the address will be deleted
     * Otherwise the address Entity will be returned as is
     *
     * @param addressEntity The Address Entity that needed to be deleted or archived
     * @return Address Entity that has been deleted or archived
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        // active 1 indicates it is not linked to any order, so it can be deleted
        if (addressEntity.getActive() == 1) {
            AddressEntity deletedAddress = addressDao.deleteAddress(addressEntity);
            return deletedAddress;
        } else {
            // active other than 1 say 0 indicates it is linked to an order and archived, so no delete happens
            return addressEntity;
        }
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
