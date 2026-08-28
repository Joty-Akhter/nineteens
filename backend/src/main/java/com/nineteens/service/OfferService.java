package com.nineteens.service;

import com.nineteens.common.exception.BadRequestException;
import com.nineteens.common.exception.NotFoundException;
import com.nineteens.domain.offer.Offer;
import com.nineteens.domain.offer.OfferRepository;
import com.nineteens.domain.offer.OfferStatus;
import com.nineteens.domain.product.Product;
import com.nineteens.domain.product.ProductRepository;
import com.nineteens.web.dto.OfferDtos;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;

    public OfferService(OfferRepository offerRepository, ProductRepository productRepository) {
        this.offerRepository = offerRepository;
        this.productRepository = productRepository;
    }

    public List<OfferDtos.OfferResponse> listPublic() {
        return offerRepository.findLive(java.time.Instant.now()).stream()
                .map(OfferDtos.OfferResponse::from)
                .toList();
    }

    public List<OfferDtos.OfferResponse> listAll() {
        return offerRepository.findAllWithProducts().stream().map(OfferDtos.OfferResponse::from).toList();
    }

    public OfferDtos.OfferResponse get(Long id) {
        return OfferDtos.OfferResponse.from(require(id));
    }

    @Transactional
    public OfferDtos.OfferResponse create(OfferDtos.OfferRequest request) {
        Offer offer = new Offer();
        apply(offer, request);
        offerRepository.save(offer);
        return OfferDtos.OfferResponse.from(offer);
    }

    @Transactional
    public OfferDtos.OfferResponse update(Long id, OfferDtos.OfferRequest request) {
        Offer offer = require(id);
        apply(offer, request);
        return OfferDtos.OfferResponse.from(offer);
    }

    @Transactional
    public void deactivate(Long id) {
        Offer offer = require(id);
        offer.setStatus(OfferStatus.INACTIVE);
    }

    private Offer require(Long id) {
        return offerRepository.findDetailedById(id).orElseThrow(() -> new NotFoundException("Offer not found"));
    }

    private void apply(Offer offer, OfferDtos.OfferRequest request) {
        if (!request.endAt().isAfter(request.startAt())) {
            throw new BadRequestException("Offer end date must be after the start date");
        }
        offer.setName(request.name().trim());
        offer.setDescription(request.description());
        offer.setDiscountType(request.discountType());
        offer.setDiscountValue(request.discountValue());
        offer.setStartAt(request.startAt());
        offer.setEndAt(request.endAt());
        offer.setStatus(request.status() == null ? OfferStatus.ACTIVE : request.status());
        Set<Long> ids = request.productIds() == null ? Set.of() : request.productIds();
        List<Product> products = ids.isEmpty() ? List.of() : productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new NotFoundException("One or more products were not found");
        }
        offer.setProducts(new HashSet<>(products));
    }
}
