package com.antulev.promo.web;

import com.antulev.promo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminProductControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ProductRepository products;

    @Test
    void admin_can_create_list_delete_products_with_pagination() throws Exception {
        mvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                          {"name":"Shoes A","category":"SPORTS-EVENTS","priceCents":1999,"stock":10,"active":true}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));

        mvc.perform(get("/api/admin/products?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)));
        Long id = products.findAll().get(0).getId();
        mvc.perform(put("/api/admin/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                          {"name":"Shoes B","category":"SPORTS-EVENTS","priceCents":2999,"stock":5,"active":false}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Shoes B")))
                .andExpect(jsonPath("$.category", is("SPORTS-EVENTS")))
                .andExpect(jsonPath("$.priceCents", is(2999)))
                .andExpect(jsonPath("$.stock", is(5)))
                .andExpect(jsonPath("$.active", is(false)));
        
        mvc.perform(delete("/api/admin/products/{id}", id))
                .andExpect(status().isOk());
    }
}
