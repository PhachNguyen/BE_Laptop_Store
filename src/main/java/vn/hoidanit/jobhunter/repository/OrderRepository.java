package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hoidanit.jobhunter.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Nếu cần thêm custom query thì thêm sau
}
