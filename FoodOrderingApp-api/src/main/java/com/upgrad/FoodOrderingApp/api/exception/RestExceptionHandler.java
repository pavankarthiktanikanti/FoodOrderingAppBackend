package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
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
     * @param exception The Authentication Exception occurred int he application
     * @param request   The web request information if any to be used while framing the response
     * @return The Error Response consisting of the Http status code and an error message
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }
}