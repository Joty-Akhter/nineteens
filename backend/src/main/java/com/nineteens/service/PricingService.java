package com.nineteens.service;

import com.nineteens.domain.catalog.PricingCalculator;
import com.nineteens.domain.offer.Offer;
import com.nineteens.domain.offer.OfferRepository;
import com.nineteens.domain.product.Product;
import com.nineteens.web.dto.ProductDtos;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private final OfferRepository offerRepository;

    public PricingService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> liveOffers() {
        return offerRepository.findLive(Instant.now());
    }

    public ProductDtos.ProductResponse toResponse(Product product) {
        return ProductDtos.ProductResponse.from(product, PricingCalculator.breakdown(product, liveOffers()));
    }

    public List<ProductDtos.ProductResponse> toResponses(List<Product> products) {
        List<Offer> live = liveOffers();
        return products.stream()
                .map(product -> ProductDtos.ProductResponse.from(product, PricingCalculator.breakdown(product, live)))
                .toList();
    }

    public PricingCalculator.PriceBreakdown breakdown(Product product) {
        return PricingCalculator.breakdown(product, liveOffers());
    }
}
