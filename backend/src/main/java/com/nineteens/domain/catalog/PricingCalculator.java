package com.nineteens.domain.catalog;

import com.nineteens.domain.offer.Offer;
import com.nineteens.domain.product.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class PricingCalculator {

    private PricingCalculator() {
    }

    public static PriceBreakdown breakdown(Product product, List<Offer> liveOffers) {
        BigDecimal original = product.getPrice();
        BigDecimal best = original;
        String offerName = null;

        if (product.getSalePrice() != null && product.getSalePrice().compareTo(best) < 0) {
            best = product.getSalePrice();
        }

        for (Offer offer : liveOffers) {
            if (!offer.appliesTo(product)) {
                continue;
            }
            BigDecimal discounted = offer.applyTo(original);
            if (discounted.compareTo(best) < 0) {
                best = discounted;
                offerName = offer.getName();
            }
        }

        BigDecimal discount = original.subtract(best).max(BigDecimal.ZERO);
        int percent = original.compareTo(BigDecimal.ZERO) == 0
                ? 0
                : discount.multiply(new BigDecimal("100")).divide(original, 0, RoundingMode.HALF_UP).intValue();
        return new PriceBreakdown(original, best, discount, percent, offerName);
    }

    public record PriceBreakdown(
            BigDecimal originalPrice,
            BigDecimal effectivePrice,
            BigDecimal discountAmount,
            int discountPercent,
            String appliedOfferName
    ) {
        public boolean onSale() {
            return discountAmount.compareTo(BigDecimal.ZERO) > 0;
        }
    }
}
