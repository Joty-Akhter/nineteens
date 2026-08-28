package com.nineteens.service;

import com.nineteens.common.exception.BadRequestException;
import com.nineteens.common.exception.ConflictException;
import com.nineteens.common.exception.NotFoundException;
import com.nineteens.common.util.SlugUtil;
import com.nineteens.domain.category.Category;
import com.nineteens.domain.category.CategoryRepository;
import com.nineteens.domain.category.CategoryStatus;
import com.nineteens.domain.product.ProductRepository;
import com.nineteens.web.dto.CategoryDtos;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDtos.CategoryResponse> listPublic() {
        return categoryRepository.findByStatusOrderByNameAsc(CategoryStatus.ACTIVE).stream()
                .map(CategoryDtos.CategoryResponse::from)
                .toList();
    }

    public List<CategoryDtos.CategoryResponse> listAll() {
        return categoryRepository.findAll().stream().map(CategoryDtos.CategoryResponse::from).toList();
    }

    public CategoryDtos.CategoryResponse getBySlug(String slug) {
        Category category = categoryRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (category.getStatus() != CategoryStatus.ACTIVE) {
            throw new NotFoundException("Category not found");
        }
        return CategoryDtos.CategoryResponse.from(category);
    }

    public Category require(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Transactional
    public CategoryDtos.CategoryResponse create(CategoryDtos.CategoryRequest request) {
        Category category = new Category();
        apply(category, request, true);
        categoryRepository.save(category);
        return CategoryDtos.CategoryResponse.from(category);
    }

    @Transactional
    public CategoryDtos.CategoryResponse update(Long id, CategoryDtos.CategoryRequest request) {
        Category category = require(id);
        apply(category, request, false);
        return CategoryDtos.CategoryResponse.from(category);
    }

    @Transactional
    public void deactivate(Long id) {
        Category category = require(id);
        if (productRepository.existsByCategoryId(id)) {
            category.setStatus(CategoryStatus.INACTIVE);
            return;
        }
        category.setStatus(CategoryStatus.INACTIVE);
    }

    private void apply(Category category, CategoryDtos.CategoryRequest request, boolean creating) {
        category.setName(request.name().trim());
        String slug = request.slug() == null || request.slug().isBlank()
                ? SlugUtil.slugify(request.name())
                : SlugUtil.slugify(request.slug());
        if (slug.isBlank()) {
            throw new BadRequestException("A valid slug is required");
        }
        boolean taken = creating
                ? categoryRepository.existsBySlug(slug)
                : categoryRepository.existsBySlugAndIdNot(slug, category.getId());
        if (taken) {
            throw new ConflictException("Category slug is already in use");
        }
        category.setSlug(slug);
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setStatus(request.status() == null ? CategoryStatus.ACTIVE : request.status());
    }
}
