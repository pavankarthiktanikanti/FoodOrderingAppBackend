package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Global Exception handler for Sign up Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Sign up Exception occurred in the application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Global Exception handler for Login Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Authentication Exception occurred in the application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Global Exception handler for authorization Failures when using jwt access token
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Authorization Exception occurred in the application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    /**
     * Global Exception handler for Save Address Failure
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Save Address Exception occurred in the application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> saveAddressException(SaveAddressException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Global Exception handler for  Address Not Found Exception
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The  Address Not Found Exception occurred in the application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(AddressNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Global Exception handler for update Customer Failures when updating name or password
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Customer Data Update Exception occurred in the application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException(UpdateCustomerException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Global Exception handler for coupon search Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Coupon Exception when the search doesn't yield results
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> couponNotFoundException(CouponNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Global Exception handler for category search Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Category Not Found Exception when there is no match for the uuid
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> categoryNotFoundException(CategoryNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Global Exception handler for Payment method search Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Payment Not Found Exception when there is no match for the uuid
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> paymentMethodNotFoundException(PaymentMethodNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Global Exception handler for restaurant search Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Restaurant Not Found Exception when there is no match for the uuid
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(RestaurantNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Global Exception handler for item search Failures
     * Handles the exception and sends back the customer/client a user friendly message along with HTTP Status code
     *
     * @param exception The Item Not Found Exception when there is no match for the uuid
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> itemNotFoundException(ItemNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Global Exception handler for invalid rating
     *
     * @param exception Invalid Rating Exception when the the customer rating field entered by the customer is empty or is not in the range of 1 to 5
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(InvalidRatingException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }
}