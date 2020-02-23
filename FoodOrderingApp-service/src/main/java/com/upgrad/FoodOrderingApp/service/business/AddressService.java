package com.upgrad.FoodOrderingApp.service.business;


import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * This will return all the states
     *
     * @return List of all states
     */
    public List<StateEntity> getAllStates() {
        return addressDao.getAllStates();
    }
}
