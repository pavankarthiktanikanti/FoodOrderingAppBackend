package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    /**
     * Retrieve the Coupon Information matched with the Coupon name passed
     * @param couponName The coupon name for which the coupon details has to be retrieved
     * @return The Coupon info matched with the coupon name
     * @throws CouponNotFoundException If the coupon name doesn't match with the Database records
     */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        CouponEntity coupon = orderDao.getCouponByCouponName(couponName);
        // No match with the Database for the coupon name
        if (coupon == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return coupon;
    }
}
