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
class DealActiveWindowTest {

    @Autowired
    PromotionEngine engine;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    DealRepository dealRepo;
    Product p;

    @BeforeEach
    void init() {
        p = new Product();
        p.setName("A");
        p.setCategory("C");
        p.setPriceCents(1000);
        p.setStock(10);
        p.setActive(true);
        p = productRepo.save(p);
    }

    @Test
    void future_deal_is_ignored() {
        Deal d = new Deal();
        d.setName("future");
        d.setType(DealType.BUY_X_GET_Y_PCT_OFF_NEXT);
        d.setProduct(p);
        d.setBuyQty(1);
        d.setGetQty(1);
        d.setPercentOff(50);
        d.setActive(true);
        d.setStartsAt(Instant.now().plusSeconds(3600));
        d.setExpiresAt(Instant.now().plusSeconds(7200));
        dealRepo.save(d);

        BasketItem bi = new BasketItem();
        bi.setProduct(p);
        bi.setUnitPriceCents(1000);
        bi.setQuantity(2);

        List<DiscountLine> lines = engine.evaluate(List.of(bi), Instant.now());
        assertThat(lines).isEmpty();
    }

    @Test
    void expired_deal_is_ignored() {
        Deal d = new Deal();
        d.setName("expired");
        d.setType(DealType.BUY_X_GET_Y_PCT_OFF_NEXT);
        d.setProduct(p);
        d.setBuyQty(1);
        d.setGetQty(1);
        d.setPercentOff(50);
        d.setActive(true);
        d.setStartsAt(Instant.now().minusSeconds(7200));
        d.setExpiresAt(Instant.now().minusSeconds(3600));
        dealRepo.save(d);

        BasketItem bi = new BasketItem();
        bi.setProduct(p);
        bi.setUnitPriceCents(1000);
        bi.setQuantity(2);
        assertThat(engine.evaluate(List.of(bi), Instant.now())).isEmpty();
    }
}
