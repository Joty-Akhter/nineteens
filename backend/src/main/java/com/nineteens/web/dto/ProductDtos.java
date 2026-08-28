package com.nineteens.web.dto;

import com.nineteens.domain.catalog.PricingCalculator;
import com.nineteens.domain.product.Product;
import com.nineteens.domain.product.ProductImage;
import com.nineteens.domain.product.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record ProductImageResponse(Long id, String url, int sortOrder, boolean primary) {
        public static ProductImageResponse from(ProductImage image) {
            return new ProductImageResponse(image.getId(), image.getUrl(), image.getSortOrder(), image.isPrimaryImage());
        }
    }

    public record ProductResponse(
            Long id,
            String name,
            String slug,
            String description,
            BigDecimal price,
            BigDecimal salePrice,
            BigDecimal effectivePrice,
            int discountPercent,
            int stockQuantity,
            boolean inStock,
            ProductStatus status,
            CategoryDtos.CategoryResponse category,
            List<ProductImageResponse> images,
            String appliedOfferName,
            Instant createdAt
    ) {
        public static ProductResponse from(Product product, PricingCalculator.PriceBreakdown pricing) {
            return new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    product.getDescription(),
                    pricing.originalPrice(),
                    pricing.onSale() ? pricing.effectivePrice() : product.getSalePrice(),
                    pricing.effectivePrice(),
                    pricing.discountPercent(),
                    product.getStockQuantity(),
                    product.getStockQuantity() > 0 && product.getStatus() == ProductStatus.ACTIVE,
                    product.getStatus(),
                    CategoryDtos.CategoryResponse.from(product.getCategory()),
                    product.getImages().stream().map(ProductImageResponse::from).toList(),
                    pricing.appliedOfferName(),
                    product.getCreatedAt());
        }
    }

    public record ProductRequest(
            @NotBlank @Size(max = 255) String name,
            @Size(max = 280) String slug,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            @DecimalMin("0.00") BigDecimal salePrice,
            @Min(0) int stockQuantity,
            @NotNull Long categoryId,
            ProductStatus status
    ) {
    }
}
