package com.lambda.watermyplants.services;

import com.lambda.watermyplants.models.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(long id);

    User findByName(String name);

    void delete(long id);

    User save(User user);

    User update(User user, long id);

    // Delete all records for seeding database
    public void deleteAll();
}
