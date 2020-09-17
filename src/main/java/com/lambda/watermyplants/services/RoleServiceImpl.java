package com.lambda.watermyplants.services;


import com.lambda.watermyplants.exceptions.ResourceFoundException;
import com.lambda.watermyplants.exceptions.ResourceNotFoundException;
import com.lambda.watermyplants.models.Role;
import com.lambda.watermyplants.repository.RoleRepository;
import com.lambda.watermyplants.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository rolerepos;

    @Autowired
    UserRepository userrepos;

    @Autowired
    private UserAuditing userAuditing;

    @Override
    public List<Role> findAll() {
        List<Role> list = new ArrayList<>();
        rolerepos.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public Role findByRoleId(long id) {
        return rolerepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role id " + id + " not found"));
    }

    @Override
    public Role findByName(String name) {
        Role foundRole = rolerepos.findByNameIgnoreCase(name);
        if(foundRole != null){
            return foundRole;
        }else{
            throw new ResourceNotFoundException(name);
        }
    }

    @Override
    public Role save(Role role) {
        if (role.getUsers().size()>0){
            throw new ResourceFoundException("User Roles are not updated through role");
        }
        return rolerepos.save(role);
    }

    @Override
    public Role update(long id, Role role) {
        if (role.getName() == null){
            throw new ResourceNotFoundException("role name not found");
        }
        if (role.getUsers().size()>0){
            throw new ResourceFoundException("User Roles are not updated through role. They are updated through user");
        }
        //check to see if role exists
        Role newRole = findByRoleId(id);
        rolerepos.updateRoleName(userAuditing.getCurrentAuditor().get(), id, role.getName());
        return findByRoleId(id);

    }

    @Override
    public void deleteAll() {
        rolerepos.deleteAll();
    }
}
