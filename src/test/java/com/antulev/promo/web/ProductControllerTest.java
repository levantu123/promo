package com.antulev.promo.web;

import com.antulev.promo.model.Product;
import com.antulev.promo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ProductControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ProductRepository products;

  Product p1, p2, p3;

  @BeforeEach
  void seed() {
    p1 = save("Shoe A", "SPORTS-MEN", 1500, 10, true);
    p2 = save("Shoe B", "SPORTS-MEN", 4000, 0,  true);
    p3 = save("Laptop", "ELECTRONICS", 100_000, 5, true);
  }

  @Test
  void search_with_filters_and_pagination() throws Exception {
    mvc.perform(get("/api/products")
            .param("category","SPORTS-MEN")
            .param("minPriceCents","1000")
            .param("maxPriceCents","3000")
            .param("inStockOnly","true")
            .param("active","true")
            .param("page","0").param("size","5")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[*].name", contains("Shoe A")))
        .andExpect(jsonPath("$.totalElements", is(1)));
  }

  @Test
  void get_by_id() throws Exception {
    mvc.perform(get("/api/products/{id}", p3.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("Laptop")));
  }

  private Product save(String n, String c, int price, int stock, boolean active){
    Product p = new Product();
    p.setName(n); p.setCategory(c); p.setPriceCents(price); p.setStock(stock); p.setActive(active);
    return products.save(p);
  }
}
