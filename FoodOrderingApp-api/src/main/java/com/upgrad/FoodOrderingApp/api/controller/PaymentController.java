package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.business.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * This method retrieves all the Payment methods available in the Database
     * No authorization required for this endpoint
     *
     * @return The List of available payment methods from Database
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/payment")
    public ResponseEntity<PaymentListResponse> getAllPaymentMethods() {
        List<PaymentEntity> paymentMethods = paymentService.getAllPaymentMethods();
        List<PaymentResponse> paymentResponses = new ArrayList<PaymentResponse>();
        // Check if any payment methods returned from database
        if (paymentMethods != null && !paymentMethods.isEmpty()) {
            for (PaymentEntity paymentMethod : paymentMethods) {
                PaymentResponse paymentResponse = new PaymentResponse();
                paymentResponse.id(UUID.fromString(paymentMethod.getUuid())).paymentName(paymentMethod.getPaymentName());
                paymentResponses.add(paymentResponse);
            }
        }
        PaymentListResponse paymentListResponse = new PaymentListResponse();
        paymentListResponse.setPaymentMethods(paymentResponses);
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);
    }
}
