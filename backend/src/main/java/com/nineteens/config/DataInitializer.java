package com.nineteens.config;

import com.nineteens.domain.category.Category;
import com.nineteens.domain.category.CategoryRepository;
import com.nineteens.domain.category.CategoryStatus;
import com.nineteens.domain.offer.DiscountType;
import com.nineteens.domain.offer.Offer;
import com.nineteens.domain.offer.OfferRepository;
import com.nineteens.domain.offer.OfferStatus;
import com.nineteens.domain.product.Product;
import com.nineteens.domain.product.ProductImage;
import com.nineteens.domain.product.ProductRepository;
import com.nineteens.domain.product.ProductStatus;
import com.nineteens.domain.user.Role;
import com.nineteens.domain.user.User;
import com.nineteens.domain.user.UserRepository;
import com.nineteens.domain.user.UserStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OfferRepository offerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            OfferRepository offerRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.offerRepository = offerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedUsers();
        if (categoryRepository.count() > 0) {
            return;
        }
        seedCatalog();
        log.info("Seeded demo catalog, admin@nineteens.com / Admin@123 and user@nineteens.com / User@123");
    }

    private void seedUsers() {
        if (!userRepository.existsByEmailIgnoreCase("admin@nineteens.com")) {
            userRepository.save(user("admin@nineteens.com", "Admin@123", "Studio", "Admin", "01700000001", Role.ADMIN));
        }
        if (!userRepository.existsByEmailIgnoreCase("user@nineteens.com")) {
            userRepository.save(user("user@nineteens.com", "User@123", "Amina", "Rahman", "01700000002", Role.USER));
        }
    }

    private User user(String email, String password, String first, String last, String phone, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(first);
        user.setLastName(last);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private void seedCatalog() {
        Category women = category("Women", "women", "Tailored separates and easy dresses.",
                "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=1200&q=80");
        Category men = category("Men", "men", "Quiet luxury for everyday wear.",
                "https://images.unsplash.com/photo-1490578474895-aec7c82d809d?auto=format&fit=crop&w=1200&q=80");
        Category accessories = category("Accessories", "accessories", "Bags, scarves, and finishing pieces.",
                "https://images.unsplash.com/photo-1590874103328-eac38a941956?auto=format&fit=crop&w=1200&q=80");
        Category footwear = category("Footwear", "footwear", "Leather and canvas, made to last.",
                "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=1200&q=80");
        Category home = category("Home", "home", "Linens and objects for a calmer room.",
                "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=1200&q=80");
        categoryRepository.saveAll(List.of(women, men, accessories, footwear, home));

        Product linenShirt = product(
                "Sand Linen Shirt",
                "sand-linen-shirt",
                "A relaxed camp-collar shirt in washed European linen. Cut to sit off the body without looking oversized.",
                "2490",
                "1990",
                42,
                women,
                18,
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1489980557514-251d61dd3fdb?auto=format&fit=crop&w=1200&q=80");
        Product wrapDress = product(
                "Clay Wrap Dress",
                "clay-wrap-dress",
                "Soft viscose wrap with a mid-calf hem and self-belt. The kind of dress that works from market to dinner.",
                "4290",
                null,
                28,
                women,
                9,
                "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=1200&q=80");
        Product woolCoat = product(
                "Charcoal Wool Coat",
                "charcoal-wool-coat",
                "Unstructured overcoat in Italian wool. Single-breasted, with a hidden placket and roomy pockets.",
                "12990",
                "10990",
                12,
                women,
                4,
                "https://images.unsplash.com/photo-1539533018447-63fcce2678e3?auto=format&fit=crop&w=1200&q=80");
        Product oxford = product(
                "Ink Oxford Shirt",
                "ink-oxford-shirt",
                "Button-down oxford in a dense cotton weave. The collar stays put; the cloth gets better with washing.",
                "2190",
                null,
                50,
                men,
                22,
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=1200&q=80");
        Product chinos = product(
                "Stone Chino Trousers",
                "stone-chino-trousers",
                "Tapered chinos with a soft crease and hidden inner waist adjusters. Everyday trousers, properly made.",
                "2890",
                "2490",
                36,
                men,
                15,
                "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=1200&q=80");
        Product knit = product(
                "Cedar Knit Polo",
                "cedar-knit-polo",
                "Short-sleeve knit polo with a fine rib collar. Wear it like a t-shirt, look like you tried.",
                "2590",
                null,
                40,
                men,
                11,
                "https://images.unsplash.com/photo-1617137968427-85924c800a22?auto=format&fit=crop&w=1200&q=80");
        Product tote = product(
                "Canvas Market Tote",
                "canvas-market-tote",
                "Heavy cotton canvas tote with vegetable-tanned leather handles and an interior zip pocket.",
                "1890",
                "1490",
                60,
                accessories,
                31,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80");
        Product scarf = product(
                "Saffron Wool Scarf",
                "saffron-wool-scarf",
                "Brushed merino scarf with hand-rolled edges. Light enough for Dhaka winters, warm enough for hill trips.",
                "1690",
                null,
                24,
                accessories,
                7,
                "https://images.unsplash.com/photo-1520903920243-00d872a2d1c9?auto=format&fit=crop&w=1200&q=80");
        Product loafers = product(
                "Chestnut Leather Loafers",
                "chestnut-leather-loafers",
                "Penny loafers in full-grain leather with a stacked leather heel and leather lining.",
                "6990",
                "5990",
                16,
                footwear,
                6,
                "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?auto=format&fit=crop&w=1200&q=80");
        Product sneakers = product(
                "Ecru Court Sneakers",
                "ecru-court-sneakers",
                "Low-profile court sneaker in broken-in canvas with a gum sole. Quiet with denim or a dress.",
                "4590",
                null,
                20,
                footwear,
                13,
                "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=1200&q=80");
        Product throwBlanket = product(
                "River Stripe Throw",
                "river-stripe-throw",
                "Cotton throw with irregular stripes. Drape it on a sofa or the end of a bed.",
                "3290",
                "2790",
                18,
                home,
                8,
                "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?auto=format&fit=crop&w=1200&q=80");
        Product candle = product(
                "Teakwood Candle",
                "teakwood-candle",
                "Soy wax candle in smoked glass. Notes of teak, vetiver, and dry citrus.",
                "1290",
                null,
                80,
                home,
                19,
                "https://images.unsplash.com/photo-1602607387339-814927a162bb?auto=format&fit=crop&w=1200&q=80");
        productRepository.saveAll(List.of(
                linenShirt, wrapDress, woolCoat, oxford, chinos, knit, tote, scarf, loafers, sneakers, throwBlanket,
                candle));

        Offer monsoon = new Offer();
        monsoon.setName("Monsoon Edit");
        monsoon.setDescription("Fifteen percent off selected shirts, bags, and loafers through the season.");
        monsoon.setDiscountType(DiscountType.PERCENTAGE);
        monsoon.setDiscountValue(new BigDecimal("15"));
        monsoon.setStartAt(Instant.now().minus(1, ChronoUnit.DAYS));
        monsoon.setEndAt(Instant.now().plus(45, ChronoUnit.DAYS));
        monsoon.setStatus(OfferStatus.ACTIVE);
        monsoon.setProducts(Set.of(linenShirt, oxford, tote, loafers));
        offerRepository.save(monsoon);
    }

    private Category category(String name, String slug, String description, String imageUrl) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        category.setStatus(CategoryStatus.ACTIVE);
        return category;
    }

    private Product product(
            String name,
            String slug,
            String description,
            String price,
            String salePrice,
            int stock,
            Category category,
            int sold,
            String... images) {
        Product product = new Product();
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setSalePrice(salePrice == null ? null : new BigDecimal(salePrice));
        product.setStockQuantity(stock);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);
        product.setSoldCount(sold);
        for (int i = 0; i < images.length; i++) {
            ProductImage image = new ProductImage();
            image.setUrl(images[i]);
            image.setSortOrder(i);
            image.setPrimaryImage(i == 0);
            product.addImage(image);
        }
        return product;
    }
}
