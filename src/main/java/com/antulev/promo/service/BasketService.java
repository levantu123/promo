package com.antulev.promo.service;

import com.antulev.promo.model.*;
import com.antulev.promo.promo.DiscountLine;
import com.antulev.promo.promo.PromotionEngine;
import com.antulev.promo.promo.Receipt;
import com.antulev.promo.promo.ReceiptCalculator;
import com.antulev.promo.repository.BasketItemRepository;
import com.antulev.promo.repository.BasketRepository;
import com.antulev.promo.repository.ProductRepository;
import com.antulev.promo.specification.BasketItemSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.antulev.promo.specification.BasketItemSpecifications.*;
import static com.antulev.promo.specification.BasketSpecifications.*;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final ProductRepository productRepository;
    private final PromotionEngine promotionEngine;
    private final ReceiptCalculator receiptCalculator;

    @Transactional
    public Basket createBasket(String customerId) {
        Basket b = new Basket();
        b.setCustomerId(customerId);
        b.setStatus(BasketStatus.ACTIVE);
        return basketRepository.save(b);
    }

    public Basket getBasket(Long basketId) {
        return basketRepository.findById(basketId).orElseThrow();
    }

    public List<Basket> findByCustomer(String customerId) {
        return basketRepository.findAll(customerIdEquals(customerId));
    }

    public List<BasketItem> listItems(Long basketId) {
        return basketItemRepository.findAll(basketIdEquals(basketId));
    }

    @Transactional
    public Basket addItem(Long basketId, Long productId, int qty) {
        if (qty <= 0)
            throw new IllegalArgumentException("qty must be > 0");

        Basket basket = getBasket(basketId);
        int rows = productRepository.reserveStock(productId, qty);
        if (rows == 0)
            throw new IllegalStateException("INSUFFICIENT_STOCK");
        Product product = productRepository.findById(productId).orElseThrow();

        BasketItem item = basketItemRepository.findAll(
                basketIdEquals(basketId).and(productIdEquals(productId))).stream().findFirst().orElse(null);

        if (item == null) {
            item = new BasketItem();
            item.setBasket(basket);
            item.setProduct(product);
            item.setQuantity(qty);
            item.setUnitPriceCents(product.getPriceCents());
        } else {
            item.setQuantity(item.getQuantity() + qty);
        }
        basketItemRepository.save(item);
        return basket;
    }

    @Transactional
    public Basket removeItem(Long basketId, Long productId, int qty) {
        if (qty <= 0)
            throw new IllegalArgumentException("qty must be > 0");

        Basket basket = getBasket(basketId);
        BasketItem item = basketItemRepository.findAll(
                basketIdEquals(basketId).and(productIdEquals(productId))).stream().findFirst().orElseThrow();

        int newQty = item.getQuantity() - qty;
        if (newQty > 0) {
            item.setQuantity(newQty);
            basketItemRepository.save(item);
        } else {
            basketItemRepository.delete(item);
        }
        productRepository.releaseStock(productId, qty);
        return basket;
    }

    @Transactional
    public Basket clear(Long basketId) {
        Basket basket = getBasket(basketId);
        List<BasketItem> items = basketItemRepository.findAll(basketIdEquals(basketId));
        basketItemRepository.deleteAll(items);
        return basket;
    }

    @Transactional
    public Basket setStatus(Long basketId, BasketStatus status) {
        Basket basket = getBasket(basketId);
        basket.setStatus(status);
        return basketRepository.save(basket);
    }

    @Transactional(readOnly = true)
    public Receipt getReceipt(Long basketId) {
        var items = basketItemRepository.findAll(
                BasketItemSpecifications.basketIdEquals(basketId));
        List<DiscountLine> discounts = promotionEngine.evaluate(items, Instant.now());
        return receiptCalculator.calculate(items, discounts);
    }
}
