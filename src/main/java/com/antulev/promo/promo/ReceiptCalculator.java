package com.antulev.promo.promo;

import com.antulev.promo.model.BasketItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReceiptCalculator {

    public Receipt calculate(List<BasketItem> items, List<DiscountLine> discounts) {
        if (items == null)
            items = List.of();
        if (discounts == null)
            discounts = List.of();

        List<Receipt.Item> receiptItems = new ArrayList<>(items.size());
        int subtotal = 0;

        for (BasketItem it : items) {
            int qty = it.getQuantity();
            int unit = it.getUnitPriceCents();
            int line = qty * unit;
            subtotal += line;

            String name = (it.getProduct() != null) ? it.getProduct().getName() : null;
            Long pid = (it.getProduct() != null) ? it.getProduct().getId() : null;

            receiptItems.add(new Receipt.Item(pid, name, qty, unit, line));
        }

        int discountTotal = discounts.stream()
                .mapToInt(DiscountLine::amountCents)
                .sum();

        int total = Math.max(0, subtotal - discountTotal);

        return new Receipt(receiptItems, discounts, subtotal, discountTotal, total);
    }
}
