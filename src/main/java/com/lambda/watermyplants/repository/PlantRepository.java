package com.lambda.watermyplants.repository;

import com.lambda.watermyplants.models.Plant;
import org.springframework.data.repository.CrudRepository;

public interface PlantRepository extends CrudRepository<Plant, Long> {
}
