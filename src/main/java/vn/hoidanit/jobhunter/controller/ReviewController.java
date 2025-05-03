package vn.hoidanit.jobhunter.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.Review;
import vn.hoidanit.jobhunter.domain.User;
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

    // Lấy review theo sản phẩm (GET /api/v1/products/{productId}/reviews?page=0&size=5)
    @GetMapping("/{productId}/reviews")
    public Page<Review> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")); // Sắp xếp mới nhất

        return reviewService.getReviewsByProduct(productId, pageable);
    }

    // Thêm review mới cho sản phẩm (POST /api/v1/products/{productId}/reviews)
    // API: Lấy toàn bộ review có phân trang
    @GetMapping("/reviews")
    public Page<Review> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewService.getAllReviews(pageable);
    }


    @PostMapping("/{productId}/reviews")
    public Review addReview(
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
        return reviewService.saveReview(review);
    }
    // Đếm số lượng theo id
    @GetMapping("/{productId}/reviews/count")
    public long getReviewCountByProductId(@PathVariable Long productId) {
        return reviewService.countReviewsByProductId(productId);
    }
    // API: Xoá review theo ID
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
        return ResponseEntity.ok("Xoá review thành công");
    }

}

