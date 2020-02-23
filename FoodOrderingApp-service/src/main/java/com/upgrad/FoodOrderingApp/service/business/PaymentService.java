package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
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
}
