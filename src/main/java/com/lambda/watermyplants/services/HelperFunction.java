package com.lambda.watermyplants.services;

import com.lambda.watermyplants.models.ValidationError;

import java.util.List;

public interface HelperFunction {

    List<ValidationError> getConstraintViolation(Throwable cause);

    boolean isAuthorizedToMakeChange(String username);
}
