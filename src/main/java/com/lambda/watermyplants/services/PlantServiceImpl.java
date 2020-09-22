package com.lambda.watermyplants.services;

import com.lambda.watermyplants.exceptions.ResourceNotFoundException;
import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.models.User;
import com.lambda.watermyplants.repository.PlantRepository;
import org.h2.value.ValueLob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Transactional
@Service(value = "plantService")
public class PlantServiceImpl implements PlantService {

    @Autowired
    private PlantRepository plantrepos;

    @Autowired
    private UserService userService;

    @Autowired
    private HelperFunction helperFunction;

    @Override
    public List<Plant> findAll() {
        List<Plant> list = new ArrayList<>();
        plantrepos.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public Plant findPlantById(long id) {
        return plantrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant with id " + id + " Not Found"));
    }

    @Override
    public Plant findByNickname(String nickname) {
        Plant p = plantrepos.findPlantByNickname(nickname);
        if(p == null){
            throw new ResourceNotFoundException("Plant NickName " + nickname + " not found");
        }
        return p;
    }

    @Override
    public List<Plant> findByUserId(long id) {
         return plantrepos.findPlantsByUserId(id);
    }

    @Override
    public List<Plant> findByNicknameContaining(String nickname) {
        return plantrepos.findByNicknameContainingIgnoreCase(nickname.toLowerCase());
    }

    @Transactional
    @Override
    public void delete(long id) {
        if(plantrepos.findById(id).isPresent()){
            if(helperFunction.isAuthorizedToMakeChange(plantrepos.findById(id)
            .get()
            .getUser()
            .getUsername())){
                plantrepos.deleteById(id);
            }
        }else {
            throw new ResourceNotFoundException("Plant with id " + id + " not found");
        }
    }

    @Override
    public Plant save(long userid, Plant plant) {
        User currentUser = userService.findById(userid);
        Plant newPlant = new Plant();
       if(plant.getPlantid() !=0){
           plantrepos.findById(plant.getPlantid())
                   .orElseThrow(() -> new ResourceNotFoundException("Plant with " + plant.getPlantid() + " not found"));
           newPlant.setPlantid(plant.getPlantid());
       }
       if (helperFunction.isAuthorizedToMakeChange(currentUser.getUsername())){
           newPlant.setUser(plant.getUser());
           newPlant.setNickname(plant.getNickname());
           newPlant.setSpecies(plant.getSpecies());
           newPlant.setImage(plant.getImage());
           newPlant.setFrequency(plant.getFrequency());

           return plantrepos.save(newPlant);

       }else{
           throw new ResourceNotFoundException("User is not authorized to change this plant");
       }

    }

    @Override
    public Plant update(Plant plant, long id) {
        if(plantrepos.findById(id).isPresent()){
            Plant currentPlant = findPlantById(id);
            if (helperFunction.isAuthorizedToMakeChange(plantrepos.findById(id)
            .get().getUser().getUsername())){
                if(plant.getNickname() != null){
                    currentPlant.setNickname(plant.getNickname());
                }
                if(plant.getSpecies() != null) {
                    currentPlant.setSpecies(plant.getSpecies());
                }
                if(plant.getImage() != null){
                    currentPlant.setImage(plant.getImage());
                }
                if(plant.getFrequency() != 0){
                    currentPlant.setFrequency(plant.getFrequency());
                }

            }
            return plantrepos.save(currentPlant);

        }else{
            throw new ResourceNotFoundException("Plant with id " + id + " not found");
        }

    }

}
