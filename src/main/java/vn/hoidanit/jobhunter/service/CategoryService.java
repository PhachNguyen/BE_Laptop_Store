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

    public Category update(String id, Category updatedCategory) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy danh mục: " + id);
        }
        updatedCategory.setId(id);
        return categoryRepository.save(updatedCategory);
    }

    public void delete(String id) {
        categoryRepository.deleteById(id);
    }
}
