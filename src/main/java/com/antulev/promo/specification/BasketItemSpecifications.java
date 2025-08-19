package com.antulev.promo.specification;

import com.antulev.promo.model.BasketItem;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class BasketItemSpecifications {

    public Specification<BasketItem> basketIdEquals(Long basketId) {
        if (basketId == null) return null;
        return (root, q, cb) -> cb.equal(root.get("basket").get("id"), basketId);
    }

    public Specification<BasketItem> productIdEquals(Long productId) {
        if (productId == null) return null;
        return (root, q, cb) -> cb.equal(root.get("product").get("id"), productId);
    }

    public Specification<BasketItem> quantityGte(Integer minQty) {
        if (minQty == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("quantity"), minQty);
    }

    public Specification<BasketItem> unitPriceBetween(Integer minCents, Integer maxCents) {
        if (minCents == null && maxCents == null) return null;
        if (minCents != null && maxCents != null) {
            return (root, q, cb) -> cb.between(root.get("unitPriceCents"), minCents, maxCents);
        }
        if (minCents != null) {
            return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("unitPriceCents"), minCents);
        }
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("unitPriceCents"), maxCents);
    }
}
