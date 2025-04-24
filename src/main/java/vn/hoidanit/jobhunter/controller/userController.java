package vn.hoidanit.jobhunter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.service.userService;

import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.idInvalidException;

import java.io.Console;
import java.io.OutputStream;
import java.util.List;
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

    public userController(userService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    @PostMapping("/create")
    @ApiMessage("Create a new user ")
    public ResponseEntity<User> createUser(@Valid @RequestBody User postManUser) throws idInvalidException {
        // TODO: process POST request
        // Mã hóa mk khi truyền vào database.
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new idInvalidException(
                    "Email: " + postManUser.getEmail() + " đã tồn tại vui lòng sử dụng email khác");
        }
        postManUser.setPassword(passwordEncoder.encode(postManUser.getPassword()));
        this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
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



}
