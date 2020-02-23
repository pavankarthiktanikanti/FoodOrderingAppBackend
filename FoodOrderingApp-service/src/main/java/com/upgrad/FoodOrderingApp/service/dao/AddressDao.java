package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This will search for all the states in the db
     *
     * @return List of State Entity
     */
    public List<StateEntity> getAllStates() {
        return entityManager.createNamedQuery("allStates", StateEntity.class).getResultList();
    }
}
