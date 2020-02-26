package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderResponse;
import com.upgrad.FoodOrderingApp.service.business.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import com.upgrad.FoodOrderingApp.service.util.FoodOrderingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

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

    /**
     * Validates customer session and saves the order to the database
     * Requests the coupon, payment, address, restaurant, item details which are ordered by customer
     * Throw error message when the access token is invalid/expired/not present in Database
     * Error message will be thrown if any of the coupon,  payment, address, restaurant, item information
     * is not available in the database
     * coupon is optional, validation takes place only if request has coupon id
     *
     * @param authorization    The Bearer authorization token from the headers
     * @param saveOrderRequest The Request holding all the details of order
     * @return The response with the created order uuid and success message
     * @throws AuthorizationFailedException   If the token is invalid or expired or not present in Database
     * @throws CouponNotFoundException        If the Coupon uuid passed doesn't match with Database records
     * @throws AddressNotFoundException       If the Coupon uuid passed doesn't match with Database records
     * @throws PaymentMethodNotFoundException If the Payment uuid passed doesn't match with Database records
     * @throws RestaurantNotFoundException    If the Restaurant uuid passed doesn't match with Database records
     * @throws ItemNotFoundException          If the Items uuid passed doesn't match with Database records
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/order")
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        // Validate customer session
        CustomerEntity loggedInCustomer = customerService.getCustomer(FoodOrderingUtil.decodeBearerToken(authorization));
        // Check if the coupon is valid or not, in case it is passed
        CouponEntity coupon = null;
        PaymentEntity payment = null;
        AddressEntity address = null;
        RestaurantEntity restaurant = null;
        if (saveOrderRequest.getCouponId() != null) {
            coupon = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
        }
        if (saveOrderRequest.getPaymentId() != null) {
            payment = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
        } else {
            // If the input doesn't pass the payment uuid in request
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        if (saveOrderRequest.getAddressId() != null) {
            address = addressService.getAddressByUUID(saveOrderRequest.getAddressId().toString(), loggedInCustomer);
        } else {
            // If the input doesn't pass the address uuid in request
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        if (saveOrderRequest.getRestaurantId() != null) {
            restaurant = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());
        } else {
            // If the input doesn't pass the restaurant uuid in request
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        // Populate the order details and save to Database
        OrderEntity order = new OrderEntity();
        order.setCustomer(loggedInCustomer);
        order.setCoupon(coupon);
        order.setAddress(address);
        order.setPayment(payment);
        order.setRestaurant(restaurant);
        order.setBill(saveOrderRequest.getBill() != null ?
                saveOrderRequest.getBill().doubleValue() : null);
        order.setDiscount(saveOrderRequest.getDiscount() != null ?
                saveOrderRequest.getDiscount().doubleValue() : null);
        final OrderEntity savedOrder = orderService.saveOrder(order);

        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        // Iterate all the items and save to database
        if (itemQuantities != null && !itemQuantities.isEmpty()) {
            for (ItemQuantity itemQuantity : itemQuantities) {
                ItemEntity item = itemService.getItemByUUID(itemQuantity.getItemId());
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setItem(item);
                orderItem.setOrder(savedOrder);
                orderItem.setQuantity(itemQuantity.getQuantity());
                orderItem.setPrice(itemQuantity.getPrice());
                orderService.saveOrderItem(orderItem);
            }
        }

        SaveOrderResponse response = new SaveOrderResponse();
        response.id(savedOrder.getUuid()).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(response, HttpStatus.CREATED);
    }
}
