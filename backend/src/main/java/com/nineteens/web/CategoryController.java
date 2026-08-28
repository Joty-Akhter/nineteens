package com.nineteens.web;

import com.nineteens.service.CategoryService;
import com.nineteens.service.ProductService;
import com.nineteens.web.dto.CategoryDtos;
import com.nineteens.web.dto.ProductDtos;
import com.nineteens.common.dto.PageResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    public List<CategoryDtos.CategoryResponse> list() {
        return categoryService.listPublic();
    }

    @GetMapping("/{slug}")
    public CategoryDtos.CategoryResponse get(@PathVariable String slug) {
        return categoryService.getBySlug(slug);
    }

    @GetMapping("/{slug}/products")
    public PageResponse<ProductDtos.ProductResponse> products(
            @PathVariable String slug,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        categoryService.getBySlug(slug);
        return productService.search(null, slug, null, null, null, null, sort, page, size, true);
    }
}
