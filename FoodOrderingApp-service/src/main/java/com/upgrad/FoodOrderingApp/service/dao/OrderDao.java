package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the Order Information in the Database
     *
     * @param order The order details to be saved
     * @return The persisted order with id value generated
     */
    public OrderEntity saveOrderDetail(OrderEntity order) {
        entityManager.persist(order);
        return order;
    }

    /**
     * Saves the Order Item Information in the Database
     *
     * @param orderItem The order item details to be saved
     * @return The persisted order item with id value generated
     */
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItem) {
        entityManager.persist(orderItem);
        return orderItem;
    }

    /**
     * Retrieves the list of previously placed orders ordered by the date placed descending
     *
     * @param customerUUID The uuid of the customer for which orders has to be retrieved
     * @return The Order details sorted in descending order of the data placed
     */
    public List<OrderEntity> getPastOrdersByCustomerId(String customerUUID) {
        return entityManager.createNamedQuery("pastOrdersByCustomerUUID", OrderEntity.class).setParameter("customerUUID", customerUUID).getResultList();
    }
}
