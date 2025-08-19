package com.antulev.promo.specification;

import com.antulev.promo.model.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

@UtilityClass
public class ProductSpecifications {

    public Specification<Product> idIn(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return (root, q, cb) -> root.get("id").in(ids);
    }

    public Specification<Product> nameContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String like = "%" + keyword.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("name")), like);
    }

    public Specification<Product> categoryEquals(String category) {
        if (category == null || category.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("category"), category);
    }

    public Specification<Product> priceBetween(Integer minCents, Integer maxCents) {
        if (minCents == null && maxCents == null) return null;
        if (minCents != null && maxCents != null) {
            return (root, q, cb) -> cb.between(root.get("priceCents"), minCents, maxCents);
        }
        if (minCents != null) {
            return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("priceCents"), minCents);
        }
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("priceCents"), maxCents);
    }

    public Specification<Product> stockGreaterThan(int threshold) {
        return (root, q, cb) -> cb.greaterThan(root.get("stock"), threshold);
    }

    public Specification<Product> inStockOnly(Boolean only) {
        if (only == null || !only) return null;
        return stockGreaterThan(0);
    }

    public Specification<Product> isActive(Boolean active) {
        if (active == null) return null;
        return (root, q, cb) -> cb.equal(root.get("active"), active);
    }
}
