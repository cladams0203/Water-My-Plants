package com.lambda.watermyplants.repository;

import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String name);


}
