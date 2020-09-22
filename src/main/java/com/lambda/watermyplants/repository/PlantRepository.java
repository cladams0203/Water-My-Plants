package com.lambda.watermyplants.repository;

import com.lambda.watermyplants.models.Plant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlantRepository extends CrudRepository<Plant, Long> {

    Plant findPlantByNickname(String nickname);



    List<Plant> findByNicknameContainingIgnoreCase(String name);
}
