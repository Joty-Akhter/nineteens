package com.nineteens.web.admin;

import com.nineteens.service.OfferService;
import com.nineteens.web.dto.OfferDtos;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/offers")
public class AdminOfferController {

    private final OfferService offerService;

    public AdminOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public List<OfferDtos.OfferResponse> list() {
        return offerService.listAll();
    }

    @GetMapping("/{id}")
    public OfferDtos.OfferResponse get(@PathVariable Long id) {
        return offerService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDtos.OfferResponse create(@Valid @RequestBody OfferDtos.OfferRequest request) {
        return offerService.create(request);
    }

    @PutMapping("/{id}")
    public OfferDtos.OfferResponse update(
            @PathVariable Long id, @Valid @RequestBody OfferDtos.OfferRequest request) {
        return offerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        offerService.deactivate(id);
    }
}
