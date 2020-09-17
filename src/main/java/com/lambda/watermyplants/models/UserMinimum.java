package com.lambda.watermyplants.models;

import javax.validation.constraints.Email;

public class UserMinimum {
    private String username;

    @Email
    private String email;


    private String password;

}
