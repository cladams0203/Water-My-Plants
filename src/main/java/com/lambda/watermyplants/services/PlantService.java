package com.lambda.watermyplants.services;

import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface PlantService {


    List<Plant> findAll();

    Plant findPlantById(long id);

    Plant findByNickname(String nickname);

    List<Plant> findByNicknameContaining(String nickname);

    void delete(long id);

    Plant save(long userid, Plant plant);

    Plant update(Plant plant, long id);


}
