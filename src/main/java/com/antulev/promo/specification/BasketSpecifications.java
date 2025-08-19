package com.antulev.promo.specification;

import com.antulev.promo.model.Basket;
import com.antulev.promo.model.BasketStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class BasketSpecifications {

    public Specification<Basket> customerIdEquals(String customerId) {
        if (customerId == null || customerId.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("customerId"), customerId);
    }

    public Specification<Basket> statusEquals(BasketStatus status) {
        if (status == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    public Specification<Basket> containsProductId(Long productId) {
        if (productId == null) return null;
        return (root, q, cb) -> {
            var items = root.join("items");
            q.distinct(true);
            return cb.equal(items.get("product").get("id"), productId);
        };
    }
}
