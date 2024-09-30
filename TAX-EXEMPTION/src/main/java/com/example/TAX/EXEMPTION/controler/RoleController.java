package com.example.TAX.EXEMPTION.controler;

import com.example.TAX.EXEMPTION.model.Role;
import com.example.TAX.EXEMPTION.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/role")
public class RoleController {

@Autowired
private RoleRepo roleRepo;
    // CREATE a new role
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role savedRole = roleRepo.save(role);
        return ResponseEntity.ok(savedRole);
    }

    // READ all roles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepo.findAll();
        return ResponseEntity.ok(roles);
    }

    // READ a specific role by ID
    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        Optional<Role> role = roleRepo.findById(roleId);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE a role
    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRole(@PathVariable Long roleId, @RequestBody Role updatedRole) {
        return roleRepo.findById(roleId)
                .map(role -> {
                    role.setRoleName(updatedRole.getRoleName());
                    Role savedRole = roleRepo.save(role);
                    return ResponseEntity.ok(savedRole);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE a role
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        if (roleRepo.existsById(roleId)) {
            roleRepo.deleteById(roleId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
