package com.nineteens.domain.offer;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    @EntityGraph(attributePaths = "products")
    @Query("""
            select o from Offer o
            where o.status = com.nineteens.domain.offer.OfferStatus.ACTIVE
              and o.startAt <= :now
              and o.endAt >= :now
            """)
    List<Offer> findLive(Instant now);

    @EntityGraph(attributePaths = "products")
    @Query("select o from Offer o")
    List<Offer> findAllWithProducts();

    @EntityGraph(attributePaths = "products")
    @Query("select o from Offer o where o.id = :id")
    java.util.Optional<Offer> findDetailedById(Long id);
}
