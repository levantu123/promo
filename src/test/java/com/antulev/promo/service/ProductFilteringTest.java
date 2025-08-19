package com.antulev.promo.service;

import com.antulev.promo.model.Product;
import com.antulev.promo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductFilteringTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepo;

    @BeforeEach
    void seed() {
        productRepo.save(p("Shoe A", "SPORTS", 1500, 10, true));
        productRepo.save(p("Shoe B", "SPORTS", 4000, 0, true));
        productRepo.save(p("Laptop", "ELECTRONICS", 100_000, 5, true));
        productRepo.save(p("Old TV", "ELECTRONICS", 30_000, 2, false));
    }

    @Test
    void filter_by_category_price_and_stock() {
        Page<Product> sportsCheap = productService.search(
                null, "SPORTS", 1000, 3000, true, true, PageRequest.of(0, 10));
        assertThat(sportsCheap.getContent()).extracting(Product::getName)
                .contains("Shoe A");
    }

    @Test
    void filter_inactive_is_excluded_when_active_true() {
        Page<Product> electronicsActive = productService.search(
                null, "ELECTRONICS", null, null, null, true, PageRequest.of(0, 10));
        assertThat(electronicsActive.getContent()).extracting(Product::getName)
                .contains("Laptop");
        assertThat(electronicsActive.getContent()).extracting(Product::getName)
                .doesNotContain("Old TV");
    }

    private static Product p(String n, String c, int price, int stock, boolean active) {
        Product p = new Product();
        p.setName(n);
        p.setCategory(c);
        p.setPriceCents(price);
        p.setStock(stock);
        p.setActive(active);
        return p;
    }
}
