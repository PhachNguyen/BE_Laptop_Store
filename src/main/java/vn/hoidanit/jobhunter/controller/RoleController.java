package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.idInvalidException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Create role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws idInvalidException {
        if (roleService.existByName(role.getName())) {
            throw new idInvalidException("Tên role đã tồn tại.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws idInvalidException {
        Role db = roleService.fetchById(role.getId());
        if (db == null) throw new idInvalidException("Không tìm thấy role.");
        return ResponseEntity.ok(roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) throws idInvalidException {
        Role db = roleService.fetchById(id);
        if (db == null) throw new idInvalidException("Role không tồn tại.");
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @ApiMessage("List roles with pagination")
    public ResponseEntity<resultPaginationDTO> getRoles(
            Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok(roleService.getRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<Role> getById(@PathVariable long id) throws idInvalidException {
        Role db = roleService.fetchById(id);
        if (db == null) throw new idInvalidException("Role không tồn tại.");
        return ResponseEntity.ok(db);
    }
}
