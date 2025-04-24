package vn.hoidanit.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoidanit.jobhunter.domain.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    // Các method CRUD đã có sẵn từ JpaRepository
    List<Product> findAll();
    Product findProductById(Long id);
    Optional<Product> findProductByName(String name);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
