package vn.hoidanit.jobhunter.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.Review;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ReviewDTO;
import vn.hoidanit.jobhunter.service.ReviewService;
import vn.hoidanit.jobhunter.service.userService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private userService userService;

    // Lấy tất cả review của sản phẩm
    @GetMapping("/reviews")
    public Page<ReviewDTO> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewService.getAllReviews(pageable); // Trả về page các ReviewDTO
    }
    // Lấy reviews theo sản phẩm
    @GetMapping("/{productId}/reviews")
    public Page<ReviewDTO> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")); // Sắp xếp mới nhất
        return reviewService.getReviewsByProduct(productId, pageable);
    }

    // Thêm review mới cho sản phẩm
    @PostMapping("/{productId}/reviews")
    public ReviewDTO addReview(
            @PathVariable Long productId,
            @RequestBody Review review,
            @AuthenticationPrincipal Jwt jwt // Spring inject JWT
    ) {
        String email = jwt.getSubject(); // Lấy email (subject) từ token

        // Truy vấn user từ DB bằng email (chắc chắn luôn có, an toàn)
        User user = userService.handlerGetUserbyUserName(email); // hoặc userRepository.findByEmail(email).orElseThrow();

        review.setUser(user);

        Product p = new Product();
        p.setId(productId);
        review.setProduct(p);

        review.setCreatedAt(java.time.Instant.now());
        return reviewService.saveReview(review);  // Trả về ReviewDTO
    }

    // Xoá review theo ID
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
        return ResponseEntity.ok("Xoá review thành công");
    }
    // Đếm số lượng review của sản phẩm theo productId

    @GetMapping("/{productId}/reviews/count")
    public long getReviewCountByProductId(@PathVariable Long productId) {
        String a = "Test";
        return reviewService.countReviewsByProductId(productId);
    }
    // Admin trả lời đánh giá
    @PostMapping("/{productId}/reviews/{reviewId}/reply")
    public ResponseEntity<ReviewDTO> replyToReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestBody String adminReply, // Câu trả lời của admin
            @AuthenticationPrincipal Jwt jwt // Lấy JWT của admin
    ) {
        // Kiểm tra quyền admin từ JWT
        String email = jwt.getSubject(); // Lấy email từ JWT
        User admin = userService.handlerGetUserbyUserName(email); // Lấy thông tin admin

        // Kiểm tra nếu admin có quyền trả lời
        if (!admin.getRole().equals("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Gọi service để cập nhật câu trả lời
        ReviewDTO updatedReview = reviewService.replyToReview(reviewId, adminReply);

        return ResponseEntity.ok(updatedReview);
    }

}

