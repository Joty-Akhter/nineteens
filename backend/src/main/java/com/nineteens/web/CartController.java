package com.nineteens.web;

import com.nineteens.service.CartService;
import com.nineteens.web.dto.CartDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDtos.CartResponse get() {
        return cartService.getCart();
    }

    @PostMapping("/items")
    public CartDtos.CartResponse add(@Valid @RequestBody CartDtos.AddItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{id}")
    public CartDtos.CartResponse update(
            @PathVariable Long id, @Valid @RequestBody CartDtos.UpdateItemRequest request) {
        return cartService.updateItem(id, request);
    }

    @DeleteMapping("/items/{id}")
    public CartDtos.CartResponse remove(@PathVariable Long id) {
        return cartService.removeItem(id);
    }

    @DeleteMapping
    public CartDtos.CartResponse clear() {
        return cartService.clear();
    }
}
