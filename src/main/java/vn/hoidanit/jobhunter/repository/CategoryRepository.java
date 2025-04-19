package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hoidanit.jobhunter.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
