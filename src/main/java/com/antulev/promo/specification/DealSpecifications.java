package com.antulev.promo.specification;

import com.antulev.promo.model.Deal;
import com.antulev.promo.model.DealType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

@UtilityClass
public class DealSpecifications {

    public Specification<Deal> nameContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String like = "%" + keyword.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("name")), like);
    }

    public Specification<Deal> typeEquals(DealType type) {
        if (type == null) return null;
        return (root, q, cb) -> cb.equal(root.get("type"), type);
    }

    public Specification<Deal> forProductId(Long productId) {
        if (productId == null) return null;
        return (root, q, cb) -> cb.equal(root.get("product").get("id"), productId);
    }

    public Specification<Deal> activeAt(Instant at) {
        if (at == null) return null;
        return (root, q, cb) -> cb.and(
            cb.isTrue(root.get("active")),
            cb.or(cb.isNull(root.get("startsAt")), cb.lessThanOrEqualTo(root.get("startsAt"), at)),
            cb.or(cb.isNull(root.get("expiresAt")), cb.greaterThanOrEqualTo(root.get("expiresAt"), at))
        );
    }

    public Specification<Deal> isActive(Boolean active) {
        if (active == null) return null;
        return (root, q, cb) -> cb.equal(root.get("active"), active);
    }
}
