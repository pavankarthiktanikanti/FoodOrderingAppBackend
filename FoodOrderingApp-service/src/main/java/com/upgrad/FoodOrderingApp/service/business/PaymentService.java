package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    /**
     * Retrieves the list of all payment methods like COD, Credit card etc
     *
     * @return The list of payment methods
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }

    /**
     * Retrieves the Payment details from the Database based on the uuid
     *
     * @param paymentUUID The uuid of the Payment to lookup in Database
     * @return The Payment Details retrieved from Database with the matched uuid
     * @throws PaymentMethodNotFoundException If the uuid doesn't match with any Database record
     */
    public PaymentEntity getPaymentByUUID(String paymentUUID) throws PaymentMethodNotFoundException {
        PaymentEntity payment = paymentDao.getPaymentByUUID(paymentUUID);
        if (payment != null) {
            return payment;
        }
        // If no payment method available with the uuid or when input doesn't have the uuid details
        throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
    }
}
