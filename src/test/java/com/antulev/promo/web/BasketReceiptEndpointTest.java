package com.antulev.promo.web;

import com.antulev.promo.model.*;
import com.antulev.promo.repository.*;
import com.antulev.promo.service.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BasketReceiptEndpointTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    BasketRepository baskets;
    @Autowired
    ProductRepository products;
    @Autowired
    DealRepository deals;
    @Autowired
    BasketService basketService;

    Basket basket;
    Product p;

    @BeforeEach
    void setup() {
        basket = new Basket();
        basket.setCustomerId("u1");
        basket.setStatus(BasketStatus.ACTIVE);
        basket = baskets.save(basket);

        p = new Product();
        p.setName("Item");
        p.setCategory("C");
        p.setPriceCents(1000);
        p.setStock(10);
        p.setActive(true);
        p = products.save(p);

        Deal d = new Deal();
        d.setName("B1G1 50");
        d.setType(DealType.BUY_X_GET_Y_PCT_OFF_NEXT);
        d.setProduct(p);
        d.setBuyQty(1);
        d.setGetQty(1);
        d.setPercentOff(50);
        d.setActive(true);
        d.setStartsAt(Instant.now().minusSeconds(60));
        d.setExpiresAt(Instant.now().plusSeconds(60));
        deals.save(d);

        basketService.addItem(basket.getId(), p.getId(), 2);
    }

    @Test
    void receipt_has_deals_and_totals() throws Exception {
        mvc.perform(get("/api/baskets/{id}/receipt", basket.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotalCents", is(2000)))
                .andExpect(jsonPath("$.discountCents", is(500)))
                .andExpect(jsonPath("$.totalCents", is(1500)));
    }
}
