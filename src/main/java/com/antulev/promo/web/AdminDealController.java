package com.antulev.promo.web;

import com.antulev.promo.model.Deal;
import com.antulev.promo.model.DealType;
import com.antulev.promo.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/deals")
@RequiredArgsConstructor
public class AdminDealController {

    private final DealService dealService;

    @GetMapping
    public Page<Deal> search(
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) DealType type,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Instant activeAt,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return dealService.search(nameKeyword, type, productId, activeAt, active, pageable);
    }

    @GetMapping("{id}")
    public Deal get(@PathVariable Long id) {
        return dealService.get(id);
    }

    @PostMapping
    public Deal create(@RequestBody Deal deal) {
        return dealService.create(deal);
    }

    @PutMapping("{id}")
    public Deal update(@PathVariable Long id, @RequestBody Deal update) {
        return dealService.update(id, update);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        dealService.delete(id);
    }
}
