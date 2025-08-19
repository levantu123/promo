package com.antulev.promo.web;

import com.antulev.promo.model.*;
import com.antulev.promo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDealControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired
  ProductRepository products;
  Product p;

  @BeforeEach
  void setup() {

    p = new Product();
    p.setName("Item");
    p.setCategory("C");
    p.setPriceCents(1000);
    p.setStock(100);
    p.setActive(true);
    p = products.save(p);
  }

  @Test
  void create_page_update_delete_deal() throws Exception {
    String body = """
        {"name":"B1G1 50","type":"BUY_X_GET_Y_PCT_OFF_NEXT","product":{"id":%d},"buyQty":1,"getQty":1,"percentOff":50,
         "active":true,"startsAt":"%s","expiresAt":"%s"}
        """.formatted(p.getId(), Instant.now().minusSeconds(60), Instant.now().plusSeconds(3600));

    mvc.perform(post("/api/admin/deals").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", notNullValue()));

    mvc.perform(get("/api/admin/deals?page=0&size=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)));

    MvcResult res = mvc.perform(get("/api/admin/deals")
        .param("page", "0").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").isNumber())
        .andReturn();

    String responseData = res.getResponse().getContentAsString();
    Long dealId = com.jayway.jsonpath.JsonPath.parse(responseData).read("$.content[0].id", Long.class);

    mvc.perform(get("/api/admin/deals/{id}", dealId))
        .andExpect(status().isOk());

    mvc.perform(put("/api/admin/deals/{id}", dealId).contentType(MediaType.APPLICATION_JSON)
        .content(body.replace("\"percentOff\":50", "\"percentOff\":60")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.percentOff", is(60)));

    mvc.perform(delete("/api/admin/deals/{id}", dealId)).andExpect(status().isOk());
  }
}
