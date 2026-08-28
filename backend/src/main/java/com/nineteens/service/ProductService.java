package com.nineteens.service;

import com.nineteens.common.dto.PageResponse;
import com.nineteens.common.exception.BadRequestException;
import com.nineteens.common.exception.ConflictException;
import com.nineteens.common.exception.NotFoundException;
import com.nineteens.common.util.SlugUtil;
import com.nineteens.domain.category.Category;
import com.nineteens.domain.offer.Offer;
import com.nineteens.domain.product.Product;
import com.nineteens.domain.product.ProductImage;
import com.nineteens.domain.product.ProductImageRepository;
import com.nineteens.domain.product.ProductRepository;
import com.nineteens.domain.product.ProductSpecifications;
import com.nineteens.domain.product.ProductStatus;
import com.nineteens.storage.StorageService;
import com.nineteens.web.dto.ProductDtos;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryService categoryService;
    private final PricingService pricingService;
    private final StorageService storageService;

    public ProductService(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            CategoryService categoryService,
            PricingService pricingService,
            StorageService storageService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryService = categoryService;
        this.pricingService = pricingService;
        this.storageService = storageService;
    }

    public PageResponse<ProductDtos.ProductResponse> search(
            String query,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean onSale,
            String sort,
            int page,
            int size,
            boolean publicOnly) {
        Collection<Long> saleIds = pricingService.liveOffers().stream()
                .map(Offer::getProducts)
                .flatMap(Collection::stream)
                .map(Product::getId)
                .collect(Collectors.toSet());
        Specification<Product> spec = ProductSpecifications.catalog(
                query, categorySlug, minPrice, maxPrice, inStock, onSale, saleIds, publicOnly);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 48), sortBy(sort));
        Page<Product> result = productRepository.findAll(spec, pageable);
        List<Long> ids = result.getContent().stream().map(Product::getId).toList();
        Map<Long, Product> detailed = ids.isEmpty()
                ? Map.of()
                : productRepository.findDetailedByIdIn(ids).stream()
                        .collect(Collectors.toMap(Product::getId, Function.identity()));
        List<ProductDtos.ProductResponse> content = ids.stream()
                .map(detailed::get)
                .filter(p -> p != null)
                .map(pricingService::toResponse)
                .toList();
        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast());
    }

    public ProductDtos.ProductResponse getPublic(Long id) {
        Product product = requireDetailed(id);
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new NotFoundException("Product not found");
        }
        return pricingService.toResponse(product);
    }

    public ProductDtos.ProductResponse getAdmin(Long id) {
        return pricingService.toResponse(requireDetailed(id));
    }

    public List<ProductDtos.ProductResponse> featured() {
        return pricingService.toResponses(productRepository.findTop8ByStatusOrderBySoldCountDesc(ProductStatus.ACTIVE));
    }

    public List<ProductDtos.ProductResponse> newest() {
        return pricingService.toResponses(productRepository.findTop8ByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE));
    }

    public List<ProductDtos.ProductResponse> onSale() {
        return pricingService.toResponses(
                productRepository.findTop8ByStatusAndSalePriceIsNotNullOrderByCreatedAtDesc(ProductStatus.ACTIVE));
    }

    public List<ProductDtos.ProductResponse> related(Long productId) {
        Product product = requireDetailed(productId);
        return pricingService.toResponses(productRepository.findTop8ByStatusAndCategoryIdAndIdNot(
                ProductStatus.ACTIVE, product.getCategory().getId(), productId));
    }

    public Product require(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Product requireDetailed(Long id) {
        return productRepository.findDetailedById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Transactional
    public ProductDtos.ProductResponse create(ProductDtos.ProductRequest request) {
        Product product = new Product();
        apply(product, request, true);
        productRepository.save(product);
        return pricingService.toResponse(requireDetailed(product.getId()));
    }

    @Transactional
    public ProductDtos.ProductResponse update(Long id, ProductDtos.ProductRequest request) {
        Product product = require(id);
        apply(product, request, false);
        product.syncStockStatus();
        if (request.status() == ProductStatus.INACTIVE) {
            product.setStatus(ProductStatus.INACTIVE);
        }
        return pricingService.toResponse(requireDetailed(id));
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = require(id);
        product.setStatus(ProductStatus.INACTIVE);
    }

    @Transactional
    public ProductDtos.ProductResponse addImage(Long productId, MultipartFile file, boolean primary) {
        Product product = requireDetailed(productId);
        StorageService.StoredFile stored = storageService.store(file);
        ProductImage image = new ProductImage();
        image.setUrl(stored.publicUrl());
        image.setSortOrder(product.getImages().size());
        image.setPrimaryImage(primary || product.getImages().isEmpty());
        if (image.isPrimaryImage()) {
            product.getImages().forEach(existing -> existing.setPrimaryImage(false));
        }
        product.addImage(image);
        productImageRepository.save(image);
        return pricingService.toResponse(product);
    }

    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        Product product = requireDetailed(productId);
        ProductImage image = product.getImages().stream()
                .filter(item -> item.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Image not found"));
        product.removeImage(image);
    }

    private void apply(Product product, ProductDtos.ProductRequest request, boolean creating) {
        Category category = categoryService.require(request.categoryId());
        product.setName(request.name().trim());
        String slug = request.slug() == null || request.slug().isBlank()
                ? SlugUtil.slugify(request.name())
                : SlugUtil.slugify(request.slug());
        if (slug.isBlank()) {
            throw new BadRequestException("A valid slug is required");
        }
        boolean taken = creating
                ? productRepository.existsBySlug(slug)
                : productRepository.existsBySlugAndIdNot(slug, product.getId());
        if (taken) {
            throw new ConflictException("Product slug is already in use");
        }
        product.setSlug(slug);
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSalePrice(request.salePrice());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
        product.setStatus(request.status() == null ? ProductStatus.ACTIVE : request.status());
        product.syncStockStatus();
        if (request.status() == ProductStatus.INACTIVE) {
            product.setStatus(ProductStatus.INACTIVE);
        }
    }

    private Sort sortBy(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "popular" -> Sort.by(Sort.Direction.DESC, "soldCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
