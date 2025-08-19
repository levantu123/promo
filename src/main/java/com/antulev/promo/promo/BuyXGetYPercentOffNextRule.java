package com.antulev.promo.promo;

import com.antulev.promo.model.BasketItem;
import com.antulev.promo.model.Deal;
import com.antulev.promo.model.DealType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public class BuyXGetYPercentOffNextRule implements PromotionRule {

    @Override
    public boolean supports(Deal deal) {
        return deal != null && deal.getType() == DealType.BUY_X_GET_Y_PCT_OFF_NEXT;
    }

    @Override
    public List<DiscountLine> apply(Deal deal, List<BasketItem> items, Instant now) {
        if (deal == null || deal.getActive() == null || !deal.getActive())
            return List.of();
        if (deal.getStartsAt() != null && now.isBefore(deal.getStartsAt()))
            return List.of();
        if (deal.getExpiresAt() != null && now.isAfter(deal.getExpiresAt()))
            return List.of();
        if (deal.getProduct() == null)
            return List.of();

        BasketItem item = items.stream()
                .filter(i -> i.getProduct() != null
                        && Objects.equals(i.getProduct().getId(), deal.getProduct().getId()))
                .findFirst()
                .orElse(null);
        if (item == null)
            return List.of();

        int buy = Math.max(0, safe(deal.getBuyQty(), 1));
        int get = Math.max(0, safe(deal.getGetQty(), 1));
        int pct = Math.max(0, Math.min(100, safe(deal.getPercentOff(), 0)));
        int qty = Math.max(0, safe(item.getQuantity(), 0));
        int unit = Math.max(0, safe(item.getUnitPriceCents(), 0));

        int groupSize = Math.max(1, buy + get);
        int groups = qty / groupSize;
        int discountedUnits = groups * get;
        int discount = discountedUnits * unit * pct / 100;

        if (discount <= 0)
            return List.of();

        String desc = "Buy %d get %d %d%% off".formatted(buy, get, pct);
        return List.of(new DiscountLine(item.getProduct().getId(), desc, discount));
    }

    private int safe(Integer v, int def) {
        return v == null ? def : v;
    }
}
