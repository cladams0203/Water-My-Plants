package com.lambda.watermyplants.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message){
        super("Error from Water My Plants " + message);
    }
}
