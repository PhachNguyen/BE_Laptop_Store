package vn.hoidanit.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Category;
import vn.hoidanit.jobhunter.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getById(String id) {
        return categoryRepository.findById(id);
    }

    public Category create(Category category) {
        if (categoryRepository.existsById(category.getId())) {
            throw new IllegalArgumentException("ID đã tồn tại: " + category.getId());
        }
        return categoryRepository.save(category);
    }

    public Category update(String id, Category category) {
        Optional<Category> optional = categoryRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Danh mục không tồn tại");
        }

        Category existing = optional.get();
        existing.setLabel(category.getLabel()); // Chỉ cho update label
        return categoryRepository.save(existing);
    }


    public void delete(String id) {
        categoryRepository.deleteById(id);
    }
}
