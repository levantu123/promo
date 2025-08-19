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
class ReceiptMultipleLinesTest {

  @Autowired ProductRepository productRepo;
  @Autowired DealRepository dealRepo;
  @Autowired PromotionEngine engine;
  @Autowired ReceiptCalculator calculator;

  Product p1, p2;

  @BeforeEach
  void seed() {
    p1 = save("P1", 1000); p2 = save("P2", 500);

    // P1: B1G1 50%
    dealRepo.save(deal(p1, 1,1,50));
    // P2: B2G1 FREE
    dealRepo.save(deal(p2, 2,1,100));
  }

  @Test
  void discounts_stack_across_different_products() {
    BasketItem i1 = bi(p1, 2, 1000);
    BasketItem i2 = bi(p2, 3,  500);
    var lines = engine.evaluate(List.of(i1, i2), Instant.now());
    Receipt r = calculator.calculate(List.of(i1, i2), lines);

    assertThat(r.subtotalCents()).isEqualTo(3500);
    assertThat(r.discountCents()).isEqualTo(1000);
    assertThat(r.totalCents()).isEqualTo(2500);
  }

  private Product save(String name, int cents) {
    Product p = new Product(); p.setName(name); p.setCategory("C"); p.setPriceCents(cents); p.setStock(50); p.setActive(true);
    return productRepo.save(p);
  }
  private Deal deal(Product p, int buy, int get, int pct) {
    Deal d = new Deal(); d.setName("D-"+p.getName()); d.setType(DealType.BUY_X_GET_Y_PCT_OFF_NEXT);
    d.setProduct(p); d.setBuyQty(buy); d.setGetQty(get); d.setPercentOff(pct);
    d.setActive(true); d.setStartsAt(Instant.now().minusSeconds(60)); d.setExpiresAt(Instant.now().plusSeconds(3600));
    return d;
  }
  private BasketItem bi(Product p, int qty, int unit) {
    BasketItem bi = new BasketItem(); bi.setProduct(p); bi.setQuantity(qty); bi.setUnitPriceCents(unit); return bi;
  }
}
