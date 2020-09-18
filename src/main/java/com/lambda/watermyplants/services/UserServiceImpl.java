package com.lambda.watermyplants.services;

import com.lambda.watermyplants.exceptions.ResourceNotFoundException;
import com.lambda.watermyplants.models.Plant;
import com.lambda.watermyplants.models.Role;
import com.lambda.watermyplants.models.User;
import com.lambda.watermyplants.models.UserRoles;
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

    @Autowired
    private RoleService roleService;

    @Autowired
    private HelperFunction helperFunction;

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
        newUser.setPasswordNoEncrypt(user.getPassword());
        newUser.setPhone(user.getPhone());

        newUser.getRoles().clear();
        for (UserRoles ur : user.getRoles()){
            Role addRole = roleService.findByRoleId(ur.getRole().getRoleid());
            newUser.getRoles().add(new UserRoles(newUser, addRole));
        }
        newUser.getUserplants().clear();
        for (Plant p : user.getUserplants()){
            newUser.getUserplants().add(new Plant(newUser,
                    p.getNickname(),
                    p.getSpecies(),
                    p.getImage(),
                    p.getFrequency()));
        }
        return userrepos.save(newUser);
    }

    @Override
    public User update(User user, long id) {

        User currentUser = findById(id);
        if(helperFunction.isAuthorizedToMakeChange(currentUser.getUsername())){
            if(user.getUsername() != null){
                currentUser.setUsername(user.getUsername().toLowerCase());
            }
            if(user.getPassword() != null){
                currentUser.setPasswordNoEncrypt(user.getPassword());
            }
            if(user.getEmail() != null){
                currentUser.setEmail(user.getEmail().toLowerCase());
            }
            if(user.getPhone() != null) {
                currentUser.setPhone(user.getPhone());
            }
            if(user.getRoles().size() > 0){
                currentUser.getRoles().clear();
                for(UserRoles ur : user.getRoles()){
                    Role addRole = roleService.findByRoleId(ur.getRole().getRoleid());
                    currentUser.getRoles().add(new UserRoles(currentUser, addRole));
                }
            }
            if(user.getUserplants().size() > 0){
                currentUser.getUserplants().clear();
                for (Plant p : user.getUserplants()){
                    currentUser.getUserplants().add(new Plant(currentUser,
                            p.getNickname(),
                            p.getSpecies(),
                            p.getImage(),
                            p.getFrequency()));
                }
            }
            return userrepos.save(currentUser);


        }else{
            throw new ResourceNotFoundException("THe user is not authorized to make change");
        }
    }

    @Transactional
    @Override
    public void deleteAll() {
        userrepos.deleteAll();
    }
}
