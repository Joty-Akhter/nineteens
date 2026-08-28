package com.nineteens.service;

import com.nineteens.common.exception.BadRequestException;
import com.nineteens.common.exception.NotFoundException;
import com.nineteens.domain.cart.Cart;
import com.nineteens.domain.cart.CartItem;
import com.nineteens.domain.cart.CartRepository;
import com.nineteens.domain.catalog.PricingCalculator;
import com.nineteens.domain.product.Product;
import com.nineteens.domain.user.User;
import com.nineteens.domain.user.UserRepository;
import com.nineteens.security.CurrentUser;
import com.nineteens.web.dto.CartDtos;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PricingService pricingService;
    private final CurrentUser currentUser;

    public CartService(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductService productService,
            PricingService pricingService,
            CurrentUser currentUser) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.pricingService = pricingService;
        this.currentUser = currentUser;
    }

    public CartDtos.CartResponse getCart() {
        return toResponse(getOrCreate());
    }

    @Transactional
    public CartDtos.CartResponse addItem(CartDtos.AddItemRequest request) {
        Cart cart = getOrCreate();
        Product product = productService.require(request.productId());
        assertPurchasable(product, request.quantity());
        BigDecimal unitPrice = pricingService.breakdown(product).effectivePrice();
        cart.findItem(product.getId()).ifPresentOrElse(
                item -> {
                    int next = item.getQuantity() + request.quantity();
                    assertPurchasable(product, next);
                    item.setQuantity(next);
                    item.setUnitPrice(unitPrice);
                },
                () -> {
                    CartItem item = new CartItem();
                    item.setProduct(product);
                    item.setQuantity(request.quantity());
                    item.setUnitPrice(unitPrice);
                    cart.addItem(item);
                });
        cartRepository.save(cart);
        return toResponse(reload());
    }

    @Transactional
    public CartDtos.CartResponse updateItem(Long itemId, CartDtos.UpdateItemRequest request) {
        Cart cart = getOrCreate();
        CartItem item = cart.getItems().stream()
                .filter(existing -> existing.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        assertPurchasable(item.getProduct(), request.quantity());
        item.setQuantity(request.quantity());
        item.setUnitPrice(pricingService.breakdown(item.getProduct()).effectivePrice());
        return toResponse(reload());
    }

    @Transactional
    public CartDtos.CartResponse removeItem(Long itemId) {
        Cart cart = getOrCreate();
        CartItem item = cart.getItems().stream()
                .filter(existing -> existing.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        cart.removeItem(item);
        return toResponse(reload());
    }

    @Transactional
    public CartDtos.CartResponse clear() {
        Cart cart = getOrCreate();
        cart.clearItems();
        return toResponse(reload());
    }

    public Cart requireForCheckout() {
        Cart cart = reload();
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }
        return cart;
    }

    @Transactional
    public void clearAfterCheckout(Cart cart) {
        cart.clearItems();
    }

    private Cart getOrCreate() {
        return cartRepository.findDetailedByUserId(currentUser.id()).orElseGet(() -> {
            User user = userRepository
                    .findById(currentUser.id())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private Cart reload() {
        return cartRepository
                .findDetailedByUserId(currentUser.id())
                .orElseThrow(() -> new NotFoundException("Cart not found"));
    }

    private void assertPurchasable(Product product, int quantity) {
        if (!product.isPurchasable()) {
            throw new BadRequestException(product.getName() + " is not available");
        }
        if (quantity > product.getStockQuantity()) {
            throw new BadRequestException("Only " + product.getStockQuantity() + " left in stock for " + product.getName());
        }
    }

    private CartDtos.CartResponse toResponse(Cart cart) {
        List<CartDtos.CartItemResponse> items = cart.getItems().stream()
                .map(item -> {
                    PricingCalculator.PriceBreakdown pricing = pricingService.breakdown(item.getProduct());
                    BigDecimal unit = pricing.effectivePrice();
                    String primary = item.getProduct().getImages().stream()
                            .filter(img -> img.isPrimaryImage())
                            .map(img -> img.getUrl())
                            .findFirst()
                            .orElseGet(() -> item.getProduct().getImages().stream()
                                    .map(img -> img.getUrl())
                                    .findFirst()
                                    .orElse(null));
                    return new CartDtos.CartItemResponse(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            primary,
                            item.getQuantity(),
                            item.getProduct().getStockQuantity(),
                            unit,
                            unit.multiply(BigDecimal.valueOf(item.getQuantity())));
                })
                .toList();
        BigDecimal subtotal = items.stream()
                .map(item -> {
                    Product product = cart.findItem(item.productId()).orElseThrow().getProduct();
                    return product.getPrice().multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = items.stream()
                .map(CartDtos.CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = subtotal.subtract(total).max(BigDecimal.ZERO);
        int count = items.stream().mapToInt(CartDtos.CartItemResponse::quantity).sum();
        return new CartDtos.CartResponse(cart.getId(), items, count, subtotal, discount, total);
    }
}
