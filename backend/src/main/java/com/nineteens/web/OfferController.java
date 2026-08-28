package com.nineteens.web;

import com.nineteens.service.OfferService;
import com.nineteens.web.dto.OfferDtos;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public List<OfferDtos.OfferResponse> list() {
        return offerService.listPublic();
    }
}
