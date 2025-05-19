package vn.hoidanit.jobhunter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.userRepository;
import vn.hoidanit.jobhunter.service.userService;

import java.util.Optional;

public class UserServiceTest {

    @Mock
    private userRepository userRepository;

    @InjectMocks
    private userService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserById_Found() {
        // Tạo dữ liệu giả
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Alice");
        mockUser.setEmail("alice@gmail.com");

        // Giả lập behavior
        when(userRepository.findById(1L)).thenReturn(mockUser);

        // Gọi method thật
        User result = userService.findUserById(1L);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@gmail.com", result.getEmail());
    }


    @Test
    void testFindUserById_NotFound() {
        when(userRepository.findById(2L)).thenReturn(null); // ❌ KHÔNG đúng vì sẽ lỗi NullPointerException
    }
}
