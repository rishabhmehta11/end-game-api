package com.sensor.api;

public class ApiServicesException extends Exception {

    // Can be used for personilsed exception handing with information about it
    public ApiServicesException(String errorMessage) {
        super(errorMessage);
    }

}
