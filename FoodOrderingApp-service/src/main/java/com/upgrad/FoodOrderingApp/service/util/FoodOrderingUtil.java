package com.upgrad.FoodOrderingApp.service.util;

import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;

import java.util.regex.Pattern;

public class FoodOrderingUtil {
    public static final String BASIC_TOKEN = "Basic ";
    public static final String BEARER_TOKEN = "Bearer ";
    public static final String COLON = ":";
    public static final Long EIGHT_HOURS_IN_MILLIS = 8 * 60 * 60 * 1000L;

    /**
     * Checks if the input string is an invalid String (null or empty)
     * Mainly used to validate the request input elements
     *
     * @param value The field to be checked for validation
     * @return true if value is null or empty, false otherwise
     */
    public static Boolean isInValid(String value) {
        return (value == null || value.isEmpty());
    }

    /**
     * Check for Invalid email format using regex
     *
     * @param email The email id of the customer to be validated
     * @return true if it doesn't match the pattern, false otherwise
     */
    public static Boolean isInValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

        Pattern pattern = Pattern.compile(regex);
        return !pattern.matcher(email).matches();
    }

    /**
     * Check for invalid Contact number, whether it has only digits of length 10 or not
     *
     * @param contactNumber The mobile number used for sign up
     * @return true if it doesn't match the pattern, false otherwise
     */
    public static Boolean isInValidContactNumber(String contactNumber) {
        String regex = "\\d{10}";
        Pattern pattern = Pattern.compile(regex);
        return !pattern.matcher(contactNumber).matches();
    }

    /**
     * Check for Strong Password to match at least one Capital letter
     * at least one digit
     * at least one special character
     * at least length 8 characters
     *
     * @param password The password provided by the customer
     * @return true if it matches the expected pattern, false otherwise
     */
    public static Boolean isStrongPassword(String password) {
        // (?=.*[A-Z]) Match at least one Capital letter
        // (?=.*\d) at least one digit
        // (?=.*[#@$%&*!^]) at least one among the listed special characters
        // (.*) remaining can be any character
        // {8,} length at least 8
        String regex = "(?=.*[A-Z])(?=.*\\d)(?=.*[#@$%&*!^])(.*){8,}";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(password).matches();
    }

    /**
     * Decode the Bearer Authorization Token
     *
     * @param authorization The Bearer authorization Token from the headers
     * @return The decoded access Token
     * @throws AuthorizationFailedException If the authorization token is not in valid format (missing Bearer prefix)
     *                                      throw an error message as not logged in
     */
    public static String decodeBearerToken(String authorization) throws AuthorizationFailedException {
        try {
            String[] bearerToken = authorization.split(FoodOrderingUtil.BEARER_TOKEN);
            if (bearerToken != null && bearerToken.length > 1) {
                String accessToken = bearerToken[1];
                return accessToken;
            } else {
                throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
    }

    /**
     * Check for the valid pin code
     * contains only digit
     * length is 6
     *
     * @param pinCode The pincode provided by the customer
     * @return true if it doesn't matches the expected pattern, true otherwise
     */
    public static Boolean isInvalidPinCode(String pinCode) {
        // \b at the start and the end defines a word boundary so that it doesn't match words like text12, 9gag, 4chan et
        // \d{6}+ allows only 6 number of digits
        String regex = "(\\b\\d{6}+\\b)";
        Pattern pattern = Pattern.compile(regex);
        return !pattern.matcher(pinCode).matches();
    }
}
