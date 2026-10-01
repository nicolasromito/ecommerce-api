package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.CategoryRequest;
import com.Romito.ecommerce_api.dto.CategoryResponse;
import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada: " + id));
        return toResponse(category);
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}