package vn.hoidanit.jobhunter.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.Review;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.ReviewService;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/products")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Lấy review theo sản phẩm (GET /api/v1/products/{productId}/reviews?page=0&size=5)
    @GetMapping("/{productId}/reviews")
    public Page<Review> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewService.getReviewsByProduct(productId, pageable);
    }

    // Thêm review mới cho sản phẩm (POST /api/v1/products/{productId}/reviews)
    @PostMapping("/{productId}/reviews")
    public Review addReview(
            @PathVariable Long productId,
            @RequestBody Review review,
            @AuthenticationPrincipal User user // <-- Spring tự inject từ token!
    ) {
        // Gắn product và user cho review (nếu bạn dùng DTO, cần map trước)
        Product p = new Product();
        p.setId(productId);
        review.setProduct(p);

        review.setUser(user); // Gán user đã đăng nhập


        review.setCreatedAt(Instant.now());

        return reviewService.saveReview(review);
    }
}
