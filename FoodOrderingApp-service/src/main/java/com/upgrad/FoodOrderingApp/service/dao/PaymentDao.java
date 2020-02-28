package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    /**
     * Retrieves the payment entity with the matched uuid
     *
     * @param paymentUUID The uuid of the Payment to lookup in Database
     * @return The matched Payment Entity from Database, null otherwise
     */
    public PaymentEntity getPaymentByUUID(String paymentUUID) {
        try {
            return entityManager.createNamedQuery("paymentByUUID", PaymentEntity.class).setParameter("paymentUUID", paymentUUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
