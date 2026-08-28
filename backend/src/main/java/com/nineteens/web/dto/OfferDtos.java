package com.nineteens.web.dto;

import com.nineteens.domain.offer.DiscountType;
import com.nineteens.domain.offer.Offer;
import com.nineteens.domain.offer.OfferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public final class OfferDtos {

    private OfferDtos() {
    }

    public record OfferResponse(
            Long id,
            String name,
            String description,
            DiscountType discountType,
            BigDecimal discountValue,
            Instant startAt,
            Instant endAt,
            OfferStatus status,
            boolean live,
            List<Long> productIds
    ) {
        public static OfferResponse from(Offer offer) {
            return new OfferResponse(
                    offer.getId(),
                    offer.getName(),
                    offer.getDescription(),
                    offer.getDiscountType(),
                    offer.getDiscountValue(),
                    offer.getStartAt(),
                    offer.getEndAt(),
                    offer.getStatus(),
                    offer.isLive(Instant.now()),
                    offer.getProducts().stream().map(p -> p.getId()).toList());
        }
    }

    public record OfferRequest(
            @NotBlank String name,
            String description,
            @NotNull DiscountType discountType,
            @NotNull @DecimalMin("0.01") BigDecimal discountValue,
            @NotNull Instant startAt,
            @NotNull Instant endAt,
            OfferStatus status,
            Set<Long> productIds
    ) {
    }
}
