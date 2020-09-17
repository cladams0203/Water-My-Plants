package com.lambda.watermyplants.services;

import com.lambda.watermyplants.models.User;
import com.lambda.watermyplants.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userrepos;

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        userrepos.findAll().iterator().forEachRemaining(users::add);
        return users;
    }

    @Override
    public User findById(long id) {
        return userrepos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id " + id + " not found"));
    }

    @Override
    public User findByName(String name) {
        User foundUser = userrepos.findByUsername(name.toLowerCase());
        if (foundUser == null){
            throw new EntityNotFoundException("User name " + name + " not found");
        }
        return foundUser;
    }

    @Transactional
    @Override
    public void delete(long id) {
        userrepos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id " + id + " not found"));
        userrepos.deleteById(id);
    }

    @Transactional
    @Override
    public User save(User user) {
        User newUser = new User();
        // Check user id to see if it is a PUT save
        if (user.getUserid() != 0){
            userrepos.findById(user.getUserid())
                    .orElseThrow(() -> new EntityNotFoundException("User id " + user.getUserid() + " not found"));
            newUser.setUserid(user.getUserid());
        }
        newUser.setUsername(user.getUsername().toLowerCase());
        newUser.setEmail(user.getEmail().toLowerCase());
        return null;
    }

    @Override
    public User update(User user, long id) {
        return null;
    }

    @Transactional
    @Override
    public void deleteAll() {
        userrepos.deleteAll();
    }
}
