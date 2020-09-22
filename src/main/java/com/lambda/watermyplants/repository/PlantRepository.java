package com.lambda.watermyplants.repository;

import com.lambda.watermyplants.models.Plant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlantRepository extends CrudRepository<Plant, Long> {

    Plant findPlantByNickname(String nickname);



    List<Plant> findByNicknameContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM plants WHERE userid = :uid", nativeQuery = true)
    List<Plant> findPlantsByUserId(long uid);

}
