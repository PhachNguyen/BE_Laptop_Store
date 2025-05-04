package vn.hoidanit.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hoidanit.jobhunter.domain.Order;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.util.constant.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserAndPaymentStatus(User user, PaymentStatus status, Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByUserAndOrderDateBetween(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findByUserAndPaymentStatusAndOrderDateBetween(
            User user, PaymentStatus paymentStatus, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

}
