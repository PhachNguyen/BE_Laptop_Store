package vn.hoidanit.jobhunter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.service.userService;

import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.idInvalidException;

import java.io.Console;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class userController {
    private final userService userService;
    private final PasswordEncoder passwordEncoder;
    private  final RoleService roleService;

    public userController(userService userService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;

    }

    // @ResponseEntity : Trả về phản hồi cho người dùng
    // CRUD user
    // @GetMapping("/users/create")
    // public String getUser() {
    // User user = new User();
    // user.setName("Phach");
    // user.setEmail("Thephach");
    // user.setPassword("123434");
    // this.userService.handleCreateUser(user);
    // return "create user";
    // }

// Tạo tk USER
    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody User postManUser) {
        // Kiểm tra email đã tồn tại chưa
        if (userService.isEmailExist(postManUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Mã hóa mật khẩu người dùng
        postManUser.setPassword(passwordEncoder.encode(postManUser.getPassword()));
        // Lấy role mặc định "USER"

        Role defaultRole = roleService.getRoleByName("USER");
        // Gán role cho người dùng mới
        postManUser.setRole(defaultRole);
        // Lưu người dùng vào database
        userService.handleCreateUser(postManUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(postManUser);
    }
//    / Tạo tk Manage
    @PostMapping("/createManage")
    public ResponseEntity<User> createManage(@RequestBody User postManUser) {
        // Kiểm tra email đã tồn tại chưa
        if (userService.isEmailExist(postManUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Mã hóa mật khẩu người dùng
        postManUser.setPassword(passwordEncoder.encode(postManUser.getPassword()));
        // Lấy role mặc định "USER"

        Role defaultRole = roleService.getRoleByName("MANAGE");
        // Gán role cho người dùng mới
        postManUser.setRole(defaultRole);
        // Lưu người dùng vào database
        userService.handleCreateUser(postManUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(postManUser);
    }
    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        User user = userService.findUserByEmail(email);  // Lấy thông tin người dùng từ service
        if (user == null) {
            throw new RuntimeException("User not found");  // Nếu không tìm thấy người dùng, trả về lỗi
        }
        return user;
    }
// Tạo tk Admin
@PostMapping("/createAdmin")
public ResponseEntity<User> createAdminUser(@RequestBody User postManUser) {
    // Kiểm tra email đã tồn tại chưa
    if (userService.isEmailExist(postManUser.getEmail())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // Mã hóa mật khẩu người dùng
    postManUser.setPassword(passwordEncoder.encode(postManUser.getPassword()));

    // Gán role ADMIN cho người dùng này
    Role adminRole = roleService.getRoleByName("ADMIN");
    postManUser.setRole(adminRole); // Gán role ADMIN

    // Lưu người dùng vào database
    userService.handleCreateUser(postManUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(postManUser);
}

    // Hàm show users
    @GetMapping("/users")
    // @CrossOrigin(origins = "http://localhost:3000")
    @ApiMessage("Fetch all users")
    public ResponseEntity<resultPaginationDTO> getUsers( // Trả về kiểu dữ lệu phân trang
            @Filter Specification<User> spec,// Lọc dữ liệu dựa trên yêu cầu sử dụng
           // Tham số này cho phép phân trang , object Pageable : sẽ được Spring tự điền voi các tham số như page,size,sort
            Pageable pageable ) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    // PathVariable: check thông tin user
    @GetMapping("/users/{id}")
    public User userById(@PathVariable("id") long id) {

        return this.userService.fetchUserById(id);
    }

    // Hàm Update
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userRequest) {
        // Gọi service để update (đã có handleUpdateUser trong service)
        userRequest.setId(id); // Đảm bảo gán id từ path vào userRequest

        User updatedUser = userService.handleUpdateUser(userRequest);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }




    // Hàm delete
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws idInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new idInvalidException("Không tồn tại user có ID: " + currentUser.getId());
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

//     Lấy token user để render ra thông tin người dùng
@GetMapping("/users/me")
@ApiMessage("Get current user information")
public ResponseEntity<User> getCurrentUser(Authentication authentication) {
    // Lấy thông tin user từ SecurityContext (đã được xác thực qua JWT token)
    String username = authentication.getName();
    User currentUser = this.userService.fetchUserByEmail(username);
    if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Trả về 404 nếu không tìm thấy user
    }

    return ResponseEntity.ok(currentUser);
}
// Update role
@PutMapping("/users/{id}/role")
public ResponseEntity<User> updateUserRole(@PathVariable long id, @RequestBody Map<String, String> roleData) {
    String newRoleName = roleData.get("role");
    User user = userService.fetchUserById(id);
    if (user == null) {
        return ResponseEntity.notFound().build();
    }
    // Lấy role từ cơ sở dữ liệu
    Role newRole = roleService.getRoleByName(newRoleName);
    if (newRole != null) {
        user.setRole(newRole);
        userService.handleCreateUser(user);
        return ResponseEntity.ok(user);
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Nếu role không hợp lệ
    }
}



}
