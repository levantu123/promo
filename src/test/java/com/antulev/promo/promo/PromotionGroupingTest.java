package com.antulev.promo.promo;

import com.antulev.promo.model.*;
import com.antulev.promo.repository.DealRepository;
import com.antulev.promo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PromotionGroupingTest {

    @Autowired
    ProductRepository productRepo;
    @Autowired
    DealRepository dealRepo;
    @Autowired
    PromotionEngine engine;

    Product p;

    @BeforeEach
    void setup() {
        p = new Product();
        p.setName("A");
        p.setCategory("C");
        p.setPriceCents(800);
        p.setStock(50);
        p.setActive(true);
        p = productRepo.save(p);

        Deal d = new Deal();
        d.setName("B2G1 Free");
        d.setType(DealType.BUY_X_GET_Y_PCT_OFF_NEXT);
        d.setProduct(p);
        d.setBuyQty(2);
        d.setGetQty(1);
        d.setPercentOff(100);
        d.setActive(true);
        d.setStartsAt(Instant.now().minusSeconds(60));
        d.setExpiresAt(Instant.now().plusSeconds(60));
        dealRepo.save(d);
    }

    @Test
    void qty7_results_in_two_free_units_and_one_full_price_remainder() {
        BasketItem bi = new BasketItem();
        bi.setProduct(p);
        bi.setUnitPriceCents(800);
        bi.setQuantity(7);

        List<DiscountLine> lines = engine.evaluate(List.of(bi), Instant.now());
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).amountCents()).isEqualTo(1600);
    }
}
