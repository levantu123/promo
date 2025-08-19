package com.antulev.promo.web;

import com.antulev.promo.model.Basket;
import com.antulev.promo.model.BasketItem;
import com.antulev.promo.model.BasketStatus;
import com.antulev.promo.promo.Receipt;
import com.antulev.promo.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/baskets")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @PostMapping
    public Basket create(@RequestParam String customerId) {
        return basketService.createBasket(customerId);
    }

    @GetMapping("{basketId}")
    public Basket get(@PathVariable Long basketId) {
        return basketService.getBasket(basketId);
    }

    @PatchMapping("{basketId}/status")
    public Basket setStatus(@PathVariable Long basketId, @RequestParam BasketStatus status) {
        return basketService.setStatus(basketId, status);
    }

    @GetMapping("/by-customer/{customerId}")
    public List<Basket> byCustomer(@PathVariable String customerId) {
        return basketService.findByCustomer(customerId);
    }

    @GetMapping("{basketId}/items")
    public List<BasketItem> items(@PathVariable Long basketId) {
        return basketService.listItems(basketId);
    }

    public record ItemRequest(Long productId, int quantity) {
    }

    @PostMapping("{basketId}/items")
    public Basket addItem(@PathVariable Long basketId, @RequestBody ItemRequest req) {
        return basketService.addItem(basketId, req.productId(), req.quantity());
    }

    @DeleteMapping("{basketId}/items")
    public Basket removeItem(@PathVariable Long basketId, @RequestBody ItemRequest req) {
        return basketService.removeItem(basketId, req.productId(), req.quantity());
    }

    @DeleteMapping("{basketId}/items/all")
    public Basket clear(@PathVariable Long basketId) {
        return basketService.clear(basketId);
    }

    @GetMapping("{basketId}/receipt")
    public Receipt getReceipt(@PathVariable Long basketId) {
        return basketService.getReceipt(basketId);
    }
}
