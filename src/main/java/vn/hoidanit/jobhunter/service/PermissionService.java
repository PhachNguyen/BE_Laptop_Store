package vn.hoidanit.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;

import vn.hoidanit.jobhunter.domain.dto.meta;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Permission create(Permission p) {
        return permissionRepository.save(p);
    }

    public Permission update(Permission p) {
        return permissionRepository.save(p);
    }

    public void delete(long id) {
        permissionRepository.deleteById(id);
    }

    public Permission fetchById(long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public boolean isPermissionExist(Permission p) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(), p.getApiPath(), p.getMethod()
        );
    }

    public boolean isSameName(Permission p) {
        return permissionRepository.findByModuleAndApiPathAndMethod(
                        p.getModule(), p.getApiPath(), p.getMethod()
                )
                .map(existing -> !existing.getName().equals(p.getName()))
                .orElse(false);
    }
// Phân trang
public resultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
    Page<Permission> page = permissionRepository.findAll(spec, pageable);

    meta metaInfo = new meta();
    metaInfo.setPage(page.getNumber());
    metaInfo.setPageSize(page.getSize());
    metaInfo.setPages(page.getTotalPages());
    metaInfo.setTotal(page.getTotalElements());

    resultPaginationDTO dto = new resultPaginationDTO();
    dto.setResult(page.getContent());
    dto.setMeta(metaInfo);

    return dto;
}
}
