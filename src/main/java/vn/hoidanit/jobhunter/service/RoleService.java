package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.meta;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.repository.RoleRepository;
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    // Kiểm tra tên role có tồn tại hay không
    public boolean existByName(String name) {
        return roleRepository.existsByName(name);
    }

    // Tạo mới role
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    // Cập nhật thông tin role
    public Role update(Role role) {
        Role roleDB = fetchById(role.getId());
        if (roleDB == null) return null;

        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());

        return roleRepository.save(roleDB);
    }

    // Xóa role
    public void delete(long id) {
        roleRepository.deleteById(id);
    }
    // Lấy role theo tên

    public Role getRoleByName(String roleName) {
        // Kiểm tra xem role đã tồn tại trong cơ sở dữ liệu hay chưa
        Role role = roleRepository.findByName(roleName).orElse(null);

        if (role == null) {
            // Nếu chưa tồn tại, tạo mới và lưu vào cơ sở dữ liệu
            role = new Role();
            role.setName(roleName);
            role.setDescription("Mô tả cho role " + roleName);
            role.setActive(true);  // Gán role này là hoạt động
            roleRepository.save(role);  // Lưu vào cơ sở dữ liệu
        }

        return role;
    }


    // Lấy role theo id
    public Role fetchById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    // Lấy danh sách role với phân trang
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
}
