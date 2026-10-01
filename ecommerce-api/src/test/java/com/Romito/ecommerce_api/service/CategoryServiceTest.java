package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.CategoryRequest;
import com.Romito.ecommerce_api.dto.CategoryResponse;
import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void creaCategoriaYDevuelveResponseConId() {
        CategoryRequest request = new CategoryRequest("Periféricos");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Periféricos");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponse response = categoryService.create(request);

        assertEquals(1L, response.id());
        assertEquals("Periféricos", response.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void findByIdLanzaExcepcionSiNoExiste() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> categoryService.findById(99L));
    }

    @Test
    void findAllDevuelveListaMapeada() {
        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("Periféricos");

        Category c2 = new Category();
        c2.setId(2L);
        c2.setName("Monitores");

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryResponse> result = categoryService.findAll();

        assertEquals(2, result.size());
        assertEquals("Periféricos", result.get(0).name());
    }
}