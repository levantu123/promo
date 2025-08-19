package com.antulev.promo.repository;

import com.antulev.promo.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long>, JpaSpecificationExecutor<BasketItem> {
}
