package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieve all the list of payment methods available in Database
     *
     * @return list of Payment Entities
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        return entityManager.createNamedQuery("allPaymentMethods", PaymentEntity.class).getResultList();
    }
}
