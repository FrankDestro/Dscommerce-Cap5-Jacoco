package com.devsuperior.dscommerce.services;


import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.factory.CategoryFactory;
import com.devsuperior.dscommerce.factory.ProductFactory;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private List<Category> listCategories;
    private Category category;
    private List<CategoryDTO> listCategoryDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;

        category = CategoryFactory.createCategory();
        listCategories = new ArrayList<>();
        listCategories.add(category);

        // repository
        when(categoryRepository.findAll()).thenReturn(listCategories);
    }

    @Test
    public void findAllShouldReturnListCategoryDTO() {
        listCategoryDTO = categoryService.findAll();

        Assertions.assertEquals(listCategoryDTO.size(), 1);
        Assertions.assertNotNull(listCategoryDTO);
        Assertions.assertEquals(listCategoryDTO.get(0).getId(), category.getId());
        Assertions.assertEquals(listCategoryDTO.get(0).getName(), category.getName());

        verify(categoryRepository, times(1)).findAll();
    }

}
