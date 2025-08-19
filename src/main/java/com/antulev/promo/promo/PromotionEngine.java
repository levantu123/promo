package com.antulev.promo.promo;

import com.antulev.promo.model.BasketItem;
import com.antulev.promo.model.Deal;
import com.antulev.promo.repository.DealRepository;
import com.antulev.promo.specification.DealSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PromotionEngine {

    private final DealRepository dealRepository;
    private final List<PromotionRule> rules;

    @Transactional(readOnly = true)
    public List<DiscountLine> evaluate(List<BasketItem> items, Instant now) {
        if (items == null || items.isEmpty()) return List.of();

        Specification<Deal> spec = Specification.allOf(
                DealSpecifications.isActive(true),
                DealSpecifications.activeAt(now)
        );
        List<Deal> deals = dealRepository.findAll(spec);

        deals.sort(Comparator.comparing(Deal::getId));

        List<DiscountLine> lines = new ArrayList<>();
        for (Deal d : deals) {
            PromotionRule handler = rules.stream().filter(r -> r.supports(d)).findFirst().orElse(null);
            if (handler == null) continue;
            lines.addAll(handler.apply(d, items, now));
        }
        return lines;
    }
}
