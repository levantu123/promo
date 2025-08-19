package com.antulev.promo.web;

import com.antulev.promo.model.Product;
import com.antulev.promo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<Product> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPriceCents,
            @RequestParam(required = false) Integer maxPriceCents,
            @RequestParam(required = false) Boolean inStockOnly,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return productService.search(keyword, category, minPriceCents, maxPriceCents, inStockOnly, active, pageable);
    }

    @GetMapping("{id}")
    public Product get(@PathVariable Long id) {
        return productService.get(id);
    }

}
