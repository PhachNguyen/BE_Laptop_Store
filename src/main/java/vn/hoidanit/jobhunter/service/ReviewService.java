package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.config.dateTimeFormatConfig;
import vn.hoidanit.jobhunter.domain.Review;
import vn.hoidanit.jobhunter.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private dateTimeFormatConfig dateTimeConfig;

    @Autowired
    private ReviewRepository reviewRepository;

    public Page<Review> getReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProduct_Id(productId, pageable);
    }

    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable); // dùng findAll có sẵn!
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Page<Review> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUser_Id(userId, pageable);
    }

    public long countReviewsByProductId(Long productId) {
        return reviewRepository.countByProductId(productId);
    }
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
