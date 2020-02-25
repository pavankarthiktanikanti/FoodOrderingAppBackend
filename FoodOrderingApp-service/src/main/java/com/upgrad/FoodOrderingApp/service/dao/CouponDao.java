package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves the Coupon Entity matched with the coupon name
     *
     * @param couponName The coupon name to be searched in Database
     * @return The Coupon Entity matched with the coupon name
     */
    public CouponEntity getCouponByCouponName(String couponName) {
        try {
            // Reading as List, if there are several records matching with the same coupon, take the first coupon record
            List<CouponEntity> couponList = entityManager.createNamedQuery("couponByCouponName", CouponEntity.class).setParameter("couponName", couponName).getResultList();
            if (couponList != null && !couponList.isEmpty()) {
                return couponList.get(0);
            } else {
                return null;
            }
        } catch (NoResultException e) {
            return null;
        }
    }
}
