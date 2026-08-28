package com.nineteens.web;

import com.nineteens.common.dto.PageResponse;
import com.nineteens.service.ProductService;
import com.nineteens.web.dto.ProductDtos;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductDtos.ProductResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return productService.search(q, category, minPrice, maxPrice, inStock, onSale, sort, page, size, true);
    }

    @GetMapping("/featured")
    public List<ProductDtos.ProductResponse> featured() {
        return productService.featured();
    }

    @GetMapping("/new")
    public List<ProductDtos.ProductResponse> newest() {
        return productService.newest();
    }

    @GetMapping("/sale")
    public List<ProductDtos.ProductResponse> sale() {
        return productService.onSale();
    }

    @GetMapping("/{id}")
    public ProductDtos.ProductResponse get(@PathVariable Long id) {
        return productService.getPublic(id);
    }

    @GetMapping("/{id}/related")
    public List<ProductDtos.ProductResponse> related(@PathVariable Long id) {
        return productService.related(id);
    }
}
