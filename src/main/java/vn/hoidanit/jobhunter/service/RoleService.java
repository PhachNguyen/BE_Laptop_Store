package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.meta;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public boolean existByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Role create(Role role) {
        attachPermissions(role);
        return roleRepository.save(role);
    }

    public Role update(Role role) {
        Role roleDB = fetchById(role.getId());
        if (roleDB == null) return null;

        attachPermissions(role);
        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());
        roleDB.setPermissions(role.getPermissions());

        return roleRepository.save(roleDB);
    }

    public void delete(long id) {
        roleRepository.deleteById(id);
    }

    public Role fetchById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public resultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = roleRepository.findAll(spec, pageable);

        meta meta = new meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        resultPaginationDTO dto = new resultPaginationDTO();
        dto.setMeta(meta);
        dto.setResult(page.getContent());

        return dto;
    }

    private void attachPermissions(Role role) {
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            List<Long> ids = role.getPermissions()
                    .stream().map(Permission::getId).collect(Collectors.toList());
            List<Permission> fullPerms = permissionRepository.findByIdIn(ids);
            role.setPermissions(fullPerms);
        }
    }
}
