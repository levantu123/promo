package com.antulev.promo.promo;

import java.util.List;

public record Receipt(
        List<Item> items,
        List<DiscountLine> discounts,
        int subtotalCents,
        int discountCents,
        int totalCents
) {
    public record Item(
            Long productId,
            String productName,
            int quantity,
            int unitPriceCents,
            int lineTotalCents
    ) {}
}
