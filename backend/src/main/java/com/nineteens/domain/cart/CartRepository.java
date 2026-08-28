package com.nineteens.domain.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.product", "items.product.images", "items.product.category"})
    @Query("select c from Cart c where c.user.id = :userId")
    Optional<Cart> findDetailedByUserId(Long userId);

    Optional<Cart> findByUserId(Long userId);
}
