package com.lambda.watermyplants.controllers;



import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.services.PlantService;


import com.lambda.watermyplants.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/plants")
public class PlantContoller {

    @Autowired
    private PlantService plantService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/plants", produces = "application/json")
    public ResponseEntity<?> getAllPlants(){
        List<Plant> allPlants = plantService.findAll();
        return new ResponseEntity<>(allPlants, HttpStatus.OK);
    }

    @GetMapping(value = "/plant/{plantid}", produces = "application/json")
    public ResponseEntity<?> getPlantById(@PathVariable long plantid){
        Plant foundPlant = plantService.findPlantById(plantid);
        return new ResponseEntity<>(foundPlant, HttpStatus.OK);
    }

    @GetMapping(value = "/user/plants")
    public ResponseEntity<?> getUserPlants(Authentication authentication){
        long userid = userService.findByName(authentication.getName()).getUserid();
        List<Plant> userPlants = plantService.findByUserId(userid);
        return new ResponseEntity<>(userPlants, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/plant", consumes = "application/json")
    public ResponseEntity<?> addPlant(@Valid @RequestBody Plant newPlant){
        newPlant.setPlantid(0);
        newPlant = plantService.save(newPlant.getUser().getUserid(), newPlant);

        HttpHeaders responseHeaders = new HttpHeaders();
        URI newPlantURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/plants/plant/{plantid}")
                .buildAndExpand(newPlant.getPlantid())
                .toUri();
        responseHeaders.setLocation(newPlantURI);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    @PostMapping(value = "/user/plants", consumes = "application/json")
    public ResponseEntity<?> addNewUserPlant(Authentication authentication, @Valid @RequestBody Plant newUserPlant){
        newUserPlant.setPlantid(0);
        newUserPlant.setUser(userService.findByName(authentication.getName()));
        newUserPlant = plantService.save(newUserPlant.getUser().getUserid(), newUserPlant);

        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        responseHeaders.setLocation(newUserURI);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(value = "/plant/{plantid}", consumes = "application/json")
    public ResponseEntity<?> replacePlant(@Valid @RequestBody Plant updatedPlant, @PathVariable long plantid){
            updatedPlant.setPlantid(plantid);
            plantService.save(updatedPlant.getUser().getUserid(), updatedPlant);

        HttpHeaders responseHeaders = new HttpHeaders();
        URI newPlantURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/plants/plant/{plantid}")
                .buildAndExpand(updatedPlant.getPlantid())
                .toUri();
        responseHeaders.setLocation(newPlantURI);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);

    }

    @PutMapping(value = "/user/plants", consumes = "application/json")
    public ResponseEntity<?> ReplaceUserPlant(Authentication authentication, @Valid @RequestBody Plant updatedUserPlant){
        long userid = userService.findByName(authentication.getName()).getUserid();
        updatedUserPlant.setUser(userService.findById(userid));
        plantService.save(userid, updatedUserPlant);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/user/plants", consumes = "application/json")
    public ResponseEntity<?> editUserPlant(Authentication authentication, @RequestBody Plant editedPlant){
        plantService.update(editedPlant, editedPlant.getPlantid() );

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/plant/{plantid}")
    public ResponseEntity<?> removeUserPlant(@PathVariable long plantid){
        plantService.delete(plantid);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
