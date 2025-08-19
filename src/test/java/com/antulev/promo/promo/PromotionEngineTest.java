package com.antulev.promo.promo;

import com.antulev.promo.model.*;
import com.antulev.promo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PromotionEngineTest {

    @Autowired
    ProductRepository productRepo;
    @Autowired
    DealRepository dealRepo;
    @Autowired
    BasketRepository basketRepo;
    @Autowired
    PromotionEngine engine;

    Product p;

    @BeforeEach
    void setup() {
        p = new Product();
        p.setName("Item");
        p.setCategory("CAT");
        p.setPriceCents(1000);
        p.setStock(100);
        p.setActive(true);
        p = productRepo.save(p);
    }

    @Test
    void buy1_get1_50pct_applies_in_window() {
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
        dealRepo.save(d);

        BasketItem bi = new BasketItem();
        bi.setProduct(p);
        bi.setQuantity(2);
        bi.setUnitPriceCents(1000);

        List<DiscountLine> lines = engine.evaluate(List.of(bi), Instant.now());
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).amountCents()).isEqualTo(500);
    }
}
