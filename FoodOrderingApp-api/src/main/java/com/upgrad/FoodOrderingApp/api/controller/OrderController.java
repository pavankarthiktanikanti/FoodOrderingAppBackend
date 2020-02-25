package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.business.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    /**
     * Validate customer session and retrieves the coupon details based on the coupon name
     * Throw error message when the access token is invalid/expired/not present in Database
     * If no coupon name matches in the database, throw error message as coupon not found
     * If the coupon name is empty, throw error message as the field should not be empty
     *
     * @param authorization The Bearer authorization token from the headers
     * @param couponName    The Coupon name for which the details has to be retrieved
     * @return The coupon details matched with the coupon name
     * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
     * @throws CouponNotFoundException      If the Coupon name is invalid or not found in Database
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = {"/order/coupon", "/order/coupon/{coupon_name}"})
    public ResponseEntity<OrderListCoupon> couponsByCouponName(@RequestHeader("authorization") final String authorization,
                                                               @PathVariable(name = "coupon_name", required = false) String couponName)
            throws AuthorizationFailedException, CouponNotFoundException {
        // Validate customer session
        customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));
        CouponEntity coupon = orderService.getCouponByCouponName(couponName);
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        orderListCoupon.id(UUID.fromString(coupon.getUuid())).couponName(coupon.getCouponName()).percent(coupon.getPercent());
        return new ResponseEntity<OrderListCoupon>(orderListCoupon, HttpStatus.OK);

    }
}
