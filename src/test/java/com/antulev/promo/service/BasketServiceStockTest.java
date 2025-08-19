package com.antulev.promo.service;

import com.antulev.promo.model.*;
import com.antulev.promo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BasketServiceStockTest {

    @Autowired
    BasketService basketService;
    @Autowired
    BasketRepository baskets;
    @Autowired
    ProductRepository products;

    Basket basket;
    Product product;

    @BeforeEach
    void init() {

        basket = new Basket();
        basket.setCustomerId("u1");
        basket.setStatus(BasketStatus.ACTIVE);
        basket = baskets.save(basket);

        product = new Product();
        product.setName("Ball");
        product.setCategory("SPORTS-EQUIPMENT");
        product.setPriceCents(1500);
        product.setStock(3);
        product.setActive(true);
        product = products.save(product);
    }

    @Test
    void add_decrements_stock_and_remove_releases_stock() {
        basketService.addItem(basket.getId(), product.getId(), 2);
        assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(1);

        basketService.removeItem(basket.getId(), product.getId(), 1);
        assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(2);
    }

    @Test
    void adding_more_than_stock_fails_and_keeps_state() {
        assertThatThrownBy(() -> basketService.addItem(basket.getId(), product.getId(), 5))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("INSUFFICIENT_STOCK");

        assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(3);
    }
}
