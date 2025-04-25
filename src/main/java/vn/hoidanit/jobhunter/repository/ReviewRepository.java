package vn.hoidanit.jobhunter.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hoidanit.jobhunter.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Lấy review theo sản phẩm có phân trang
    Page<Review> findByProduct_Id(Long productId, Pageable pageable);

    // (Nếu cần) Lấy review theo user
    Page<Review> findByUser_Id(Long userId, Pageable pageable);

    // Hàm đếm số lượng Review
    long countByProductId(Long productId);
}
