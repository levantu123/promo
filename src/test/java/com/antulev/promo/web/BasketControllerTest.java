package com.antulev.promo.web;

import com.antulev.promo.model.*;
import com.antulev.promo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class BasketControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ProductRepository products;
    @Autowired
    DealRepository deals;

    Product p;

    @BeforeEach
    void seed() {
        p = new Product();
        p.setName("Ball");
        p.setCategory("SPORTS-EQUIPMENT");
        p.setPriceCents(1000);
        p.setStock(3);
        p.setActive(true);
        p = products.save(p);
    }

    @Test
    void create_add_remove_and_receipt() throws Exception {
        String bjson = mvc.perform(post("/api/baskets").param("customerId", "u1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse().getContentAsString();
        long basketId = Long.parseLong(bjson.replaceAll("(?s).*\"id\":(\\d+).*", "$1"));

        mvc.perform(get("/api/baskets/by-customer/{customerId}", "u1"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/baskets/{id}", basketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) basketId)))
                .andExpect(jsonPath("$.customerId", is("u1")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mvc.perform(patch("/api/baskets/{id}/status", basketId).param("status", "CANCELLED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        mvc.perform(patch("/api/baskets/{id}/status", basketId).param("status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mvc.perform(post("/api/baskets/{id}/items", basketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"productId\":%d,\"quantity\":2}").formatted(p.getId())))
                .andExpect(status().isOk());
        assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(1);

        mvc.perform(get("/api/baskets/{id}/items", basketId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Deal d = new Deal();
        d.setName("B1G1");
        d.setType(DealType.BUY_X_GET_Y_PCT_OFF_NEXT);
        d.setProduct(p);
        d.setBuyQty(1);
        d.setGetQty(1);
        d.setPercentOff(50);
        d.setActive(true);
        d.setStartsAt(Instant.now().minusSeconds(60));
        d.setExpiresAt(Instant.now().plusSeconds(60));
        deals.save(d);

        mvc.perform(get("/api/baskets/{id}/receipt", basketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotalCents", is(2000)))
                .andExpect(jsonPath("$.discountCents", is(500)))
                .andExpect(jsonPath("$.totalCents", is(1500)));

        mvc.perform(delete("/api/baskets/{id}/items", basketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"productId\":%d,\"quantity\":1}").formatted(p.getId())))
                .andExpect(status().isOk());
        assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(2);

        mvc.perform(delete("/api/baskets/{basketId}/items/all", basketId))
                .andExpect(status().isOk());
    }

    @Test
    void add_more_than_stock_returns_409() throws Exception {
        String bjson = mvc.perform(post("/api/baskets").param("customerId", "u1"))
                .andReturn().getResponse().getContentAsString();
        long basketId = Long.parseLong(bjson.replaceAll("(?s).*\"id\":(\\d+).*", "$1"));

        mvc.perform(post("/api/baskets/{id}/items", basketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"productId\":%d,\"quantity\":9}").formatted(p.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("INSUFFICIENT_STOCK")));
    }
}
