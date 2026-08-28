package com.nineteens.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "payment"})
    @Query("select o from Order o where o.user.id = :userId")
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "payment", "user"})
    @Query("select o from Order o where o.id = :id")
    java.util.Optional<Order> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"items", "payment", "user"})
    @Query("select o from Order o")
    Page<Order> findAllDetailed(Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status <> com.nineteens.domain.order.OrderStatus.CANCELLED")
    java.math.BigDecimal sumNonCancelledTotals();
}
