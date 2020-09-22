package com.lambda.watermyplants.services;

import com.lambda.watermyplants.models.Role;

import java.util.List;

public interface RoleService {

    List<Role> findAll();

    Role findByRoleId(long id);

    Role findByName(String name);

    Role save(Role role);

    Role update(long id, Role role);

    //delete all for seeding

    public void deleteAll();


}
