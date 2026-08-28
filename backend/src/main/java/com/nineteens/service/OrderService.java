package com.nineteens.service;

import com.nineteens.common.dto.PageResponse;
import com.nineteens.common.exception.ApiException;
import com.nineteens.common.exception.BadRequestException;
import com.nineteens.common.exception.NotFoundException;
import com.nineteens.config.AppProperties;
import com.nineteens.domain.cart.Cart;
import com.nineteens.domain.cart.CartItem;
import com.nineteens.domain.catalog.PricingCalculator;
import com.nineteens.domain.order.CashOnDeliveryProcessor;
import com.nineteens.domain.order.Order;
import com.nineteens.domain.order.OrderItem;
import com.nineteens.domain.order.OrderRepository;
import com.nineteens.domain.order.OrderStatus;
import com.nineteens.domain.order.Payment;
import com.nineteens.domain.product.Product;
import com.nineteens.domain.user.User;
import com.nineteens.security.CurrentUser;
import com.nineteens.web.dto.OrderDtos;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final DateTimeFormatter NUMBER_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PricingService pricingService;
    private final UserService userService;
    private final CurrentUser currentUser;
    private final CashOnDeliveryProcessor paymentProcessor;
    private final AppProperties appProperties;

    public OrderService(
            OrderRepository orderRepository,
            CartService cartService,
            PricingService pricingService,
            UserService userService,
            CurrentUser currentUser,
            CashOnDeliveryProcessor paymentProcessor,
            AppProperties appProperties) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.pricingService = pricingService;
        this.userService = userService;
        this.currentUser = currentUser;
        this.paymentProcessor = paymentProcessor;
        this.appProperties = appProperties;
    }

    @Transactional
    public OrderDtos.OrderResponse checkout(OrderDtos.CheckoutRequest request) {
        Cart cart = cartService.requireForCheckout();
        User user = userService.require(currentUser.id());

        Order order = new Order();
        order.setOrderNumber(nextOrderNumber());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingName(request.recipientName().trim());
        order.setShippingPhone(request.phone().trim());
        order.setShippingAddress(request.addressLine().trim());
        order.setShippingCity(request.city().trim());
        order.setShippingPostalCode(request.postalCode().trim());
        order.setDeliveryNote(request.deliveryNote());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal payable = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isPurchasable()) {
                throw new BadRequestException(product.getName() + " is no longer available");
            }
            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }

            PricingCalculator.PriceBreakdown pricing = pricingService.breakdown(product);
            BigDecimal lineOriginal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal linePayable = pricing.effectivePrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal lineDiscount = lineOriginal.subtract(linePayable).max(BigDecimal.ZERO);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(pricing.effectivePrice());
            orderItem.setDiscount(lineDiscount);
            orderItem.setTotalPrice(linePayable);
            order.addItem(orderItem);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setSoldCount(product.getSoldCount() + cartItem.getQuantity());
            product.syncStockStatus();

            subtotal = subtotal.add(lineOriginal);
            payable = payable.add(linePayable);
        }

        BigDecimal discount = subtotal.subtract(payable).max(BigDecimal.ZERO);
        BigDecimal shipping = appProperties.getShipping().getCost();
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setShippingCost(shipping);
        order.setTotalAmount(payable.add(shipping));

        Payment payment = paymentProcessor.initiate(order);
        order.setPayment(payment);
        orderRepository.save(order);
        cartService.clearAfterCheckout(cart);
        log.info("Created order {} for user {}", order.getOrderNumber(), user.getEmail());
        return toResponse(order);
    }

    public PageResponse<OrderDtos.OrderResponse> myOrders(int page, int size) {
        Page<Order> result = orderRepository.findByUserId(
                currentUser.id(),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageResponse.from(result.map(this::toResponse));
    }

    public OrderDtos.OrderResponse myOrder(Long id) {
        Order order = requireDetailed(id);
        if (!order.getUser().getId().equals(currentUser.id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot view this order");
        }
        return toResponse(order);
    }

    public PageResponse<OrderDtos.OrderResponse> allOrders(int page, int size) {
        Page<Order> result = orderRepository.findAllDetailed(
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageResponse.from(result.map(this::toResponse));
    }

    public OrderDtos.OrderResponse getAdmin(Long id) {
        return toResponse(requireDetailed(id));
    }

    @Transactional
    public OrderDtos.OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = requireDetailed(id);
        OrderStatus previous = order.getStatus();
        if (previous == OrderStatus.CANCELLED && status != OrderStatus.CANCELLED) {
            throw new BadRequestException("A cancelled order cannot be reopened");
        }
        if (status == OrderStatus.CANCELLED && previous != OrderStatus.CANCELLED) {
            restoreStock(order);
        }
        if (status == OrderStatus.DELIVERED && order.getPayment() != null) {
            order.getPayment().setStatus(com.nineteens.domain.order.PaymentStatus.COMPLETED);
        }
        order.setStatus(status);
        return toResponse(order);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null) {
                continue;
            }
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setSoldCount(Math.max(0, product.getSoldCount() - item.getQuantity()));
            if (product.getStatus() != com.nineteens.domain.product.ProductStatus.INACTIVE) {
                product.syncStockStatus();
            }
        }
    }

    private Order requireDetailed(Long id) {
        return orderRepository.findDetailedById(id).orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private String nextOrderNumber() {
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "NT-" + LocalDateTime.now().format(NUMBER_TIME) + "-" + suffix;
    }

    private OrderDtos.OrderResponse toResponse(Order order) {
        OrderDtos.PaymentResponse payment = order.getPayment() == null
                ? null
                : new OrderDtos.PaymentResponse(
                        order.getPayment().getProvider(),
                        order.getPayment().getStatus(),
                        order.getPayment().getAmount(),
                        order.getPayment().getTransactionRef());
        return new OrderDtos.OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getSubtotal(),
                order.getDiscount(),
                order.getShippingCost(),
                order.getTotalAmount(),
                order.getShippingName(),
                order.getShippingPhone(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingPostalCode(),
                order.getDeliveryNote(),
                order.getItems().stream()
                        .map(item -> new OrderDtos.OrderItemResponse(
                                item.getId(),
                                item.getProduct() == null ? null : item.getProduct().getId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getDiscount(),
                                item.getTotalPrice()))
                        .toList(),
                payment,
                order.getCreatedAt());
    }
}
