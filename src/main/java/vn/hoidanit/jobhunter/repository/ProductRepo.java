package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoidanit.jobhunter.domain.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    // Các method CRUD đã có sẵn từ JpaRepository
}
