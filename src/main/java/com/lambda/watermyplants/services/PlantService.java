package com.lambda.watermyplants.services;

import com.lambda.watermyplants.models.Plant;

import java.util.List;

public interface PlantService {

    List<Plant> findAll();

    Plant findById(long id);

    Plant findByNickname(String nickname);

    void delete(long id);

    Plant save(Plant plant);

    Plant update(Plant plant, long id);

    // delete all for seeding database
    public void deleteAll();
}
