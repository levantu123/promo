package com.antulev.promo.promo;

import com.antulev.promo.model.BasketItem;
import com.antulev.promo.model.Deal;

import java.time.Instant;
import java.util.List;

public interface PromotionRule {

    boolean supports(Deal deal);

    List<DiscountLine> apply(Deal deal, List<BasketItem> items, Instant now);
}
