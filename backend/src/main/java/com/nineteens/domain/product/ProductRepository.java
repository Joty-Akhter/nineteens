package com.nineteens.domain.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category", "images"})
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"category", "images"})
    @Query("select p from Product p where p.id in :ids")
    List<Product> findDetailedByIdIn(Collection<Long> ids);

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findTop8ByStatusOrderBySoldCountDesc(ProductStatus status);

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findTop8ByStatusOrderByCreatedAtDesc(ProductStatus status);

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findTop8ByStatusAndSalePriceIsNotNullOrderByCreatedAtDesc(ProductStatus status);

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findTop8ByStatusAndCategoryIdAndIdNot(ProductStatus status, Long categoryId, Long id);
}
