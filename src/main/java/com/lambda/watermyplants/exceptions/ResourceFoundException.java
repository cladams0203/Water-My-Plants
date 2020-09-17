package com.lambda.watermyplants.exceptions;

public class ResourceFoundException extends RuntimeException {
    public ResourceFoundException(String message){
        super("Error from Water My Plants API " + message);
    }
}
