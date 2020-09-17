package com.lambda.watermyplants.repository;

import com.lambda.watermyplants.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
