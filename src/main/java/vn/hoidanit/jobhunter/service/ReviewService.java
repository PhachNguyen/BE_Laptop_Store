package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Review;
import vn.hoidanit.jobhunter.domain.dto.ReviewDTO;
import vn.hoidanit.jobhunter.repository.ReviewRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // Sửa phương thức chuyển đổi từ Review thành ReviewDTO
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReview(review.getReview());
        reviewDTO.setAdminReply(review.getAdminReply());

        // Chuyển đổi thời gian tạo (createdAt) thành chuỗi định dạng "yyyy-MM-dd HH:mm:ss"
        if (review.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            reviewDTO.setCreatedAt(formatter.format(review.getCreatedAt()));
        }

        return reviewDTO;
    }

    // Phương thức lấy reviews theo productId và chuyển đổi sang DTO
    public Page<ReviewDTO> getReviewsByProduct(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProduct_Id(productId, pageable);
        return reviews.map(this::convertToDTO); // Chuyển đổi từ Review thành ReviewDTO
    }

    // Phương thức lấy tất cả reviews và chuyển đổi sang DTO
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(this::convertToDTO); // Chuyển đổi từ Review thành ReviewDTO
    }

    // Phương thức lưu review và trả về ReviewDTO
    public ReviewDTO saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview); // Trả về ReviewDTO
    }

    // Lấy reviews theo userId và chuyển đổi sang DTO
    public Page<ReviewDTO> getReviewsByUser(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByUser_Id(userId, pageable);
        return reviews.map(this::convertToDTO); // Chuyển đổi từ Review thành ReviewDTO
    }
    public ReviewDTO replyToReview(Long reviewId, String adminReply) {
        // Tìm review theo ID
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));

        // Cập nhật câu trả lời của admin
        review.setAdminReply(adminReply);

        // Lưu lại review đã được cập nhật
        Review updatedReview = reviewRepository.save(review);

        // Chuyển review đã cập nhật thành ReviewDTO
        return convertToDTO(updatedReview);
    }

    // Phương thức đếm số lượng review theo productId
    public long countReviewsByProductId(Long productId) {
        return reviewRepository.countByProductId(productId);  // Giả sử bạn đã có phương thức này trong repository
    }
    public void deleteReviewById(Long id) {
        // Kiểm tra xem review có tồn tại không
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));

        // Xóa review
        reviewRepository.delete(review);
    }

}
