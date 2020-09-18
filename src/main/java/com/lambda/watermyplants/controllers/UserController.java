package com.lambda.watermyplants.controllers;


import com.lambda.watermyplants.models.User;
import com.lambda.watermyplants.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/users", produces = "application/json")
    public ResponseEntity<?> listAllUsers(){
        List<User> allUsers = userService.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/name/{userName}", produces = "application/json")
    public ResponseEntity<?> getUserByName(@PathVariable String userName){
        User u = userService.findByName(userName);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/name/like/{userName}", produces = "application/json")
    public ResponseEntity<?> getUserByLikeName(@PathVariable String userName){
        List<User> u = userService.findByNameContaining(userName);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<?> getUserByAuthenticatedName(Authentication authentication){
        User currentUser = userService.findByName(authentication.getName());
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<?> addNewUser(@Valid @RequestBody User newUser) throws URISyntaxException{
        newUser.setUserid(0);
        newUser = userService.save(newUser);

        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        responseHeaders.setLocation(newUserURI);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(value = "/user/{userid}", consumes = "application/json")
    public ResponseEntity<?> updateFullUser(@Valid @RequestBody User updateUser, @PathVariable long userid){

        updateUser.setUserid(userid);
        userService.save(updateUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<?> updateUser(Authentication authentication, @RequestBody User updateUser){
        long userId = userService.findByName(authentication.getName()).getUserid();
        User user = new User();
        user.setEmail(updateUser.getEmail());
        user.setPhone(updateUser.getPhone());
        user.setUserplants(updateUser.getUserplants());
        userService.update(user, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/user")
    public ResponseEntity<?> deleteUser(Authentication authentication){
        long userId = userService.findByName(authentication.getName()).getUserid();
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
