package com.nineteens.domain.product;

import com.nineteens.domain.category.Category;
import com.nineteens.domain.category.CategoryStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> catalog(
            String query,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean onSale,
            Collection<Long> saleProductIds,
            boolean publicOnly) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Product, Category> category = root.join("category", JoinType.INNER);

            if (publicOnly) {
                predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));
                predicates.add(cb.equal(category.get("status"), CategoryStatus.ACTIVE));
            }

            if (StringUtils.hasText(query)) {
                String like = "%" + query.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), like),
                        cb.like(cb.lower(category.get("name")), like)));
            }

            if (StringUtils.hasText(categorySlug)) {
                predicates.add(cb.equal(category.get("slug"), categorySlug));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (Boolean.TRUE.equals(inStock)) {
                predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
            }
            if (Boolean.TRUE.equals(onSale)) {
                Predicate hasSalePrice = cb.isNotNull(root.get("salePrice"));
                if (saleProductIds != null && !saleProductIds.isEmpty()) {
                    predicates.add(cb.or(hasSalePrice, root.get("id").in(saleProductIds)));
                } else {
                    predicates.add(hasSalePrice);
                }
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
