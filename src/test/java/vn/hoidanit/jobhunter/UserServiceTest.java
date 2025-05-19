package vn.hoidanit.jobhunter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.userRepository;
import vn.hoidanit.jobhunter.service.userService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class UserServiceTest {

//    Tạo một object fake, k ket nối tới db
    @Mock
    private userRepository userRepository;

    private userService userService;
// Setup trước mỗi test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new userService(userRepository); // inject mock bằng tay
    }
//     Test chức năng thêm người dùng
    @Test
    void testHandleCreateUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");

        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.handleCreateUser(newUser);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
    }
//     Test kiểm tra email có tồn tại k
    @Test
    void testIsEmailExist() {
        when(userRepository.existsByEmail("exist@example.com")).thenReturn(true);

        boolean result = userService.isEmailExist("exist@example.com");

        assertTrue(result);
    }
    @Test
    void testGetUserByRefreshTokenAndEmail() {
        User mockUser = new User();
        mockUser.setEmail("refresh@example.com");
        mockUser.setRefreshToken("abc123");

        when(userRepository.findByRefreshTokenAndEmail("abc123", "refresh@example.com"))
                .thenReturn(mockUser);

        User result = userService.getUserByRefreshTokenAndEmail("abc123", "refresh@example.com");

        assertNotNull(result);
        assertEquals("abc123", result.getRefreshToken());
        assertEquals("refresh@example.com", result.getEmail());
    }

    // Hàm test tìm kiếm theo ID
    @Test
    void testFindUserById_Found() {
        //  Tạo 1 đối tượng User giả để giả lập kết quả từ database.
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Phach");
        mockUser.setEmail("test@gmail.com");

        // Khi gọi repo -> trả về user giả
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Gọi hàm thực tế
        User result = userService.findUserById(1L);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Phach", result.getName());
        assertEquals("test@gmail.com", result.getEmail());
    }
}
