package com.antulev.promo.service;

import com.antulev.promo.model.Product;
import com.antulev.promo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.antulev.promo.specification.ProductSpecifications.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> search(
            String keyword,
            String category,
            Integer minPriceCents,
            Integer maxPriceCents,
            Boolean inStockOnly,
            Boolean active,
            Pageable pageable) {
        Specification<Product> spec = Specification.allOf(
                nameContains(keyword),
                categoryEquals(category),
                priceBetween(minPriceCents, maxPriceCents),
                inStockOnly(inStockOnly),
                isActive(active));
        return productRepository.findAll(spec, pageable);
    }

    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Product create(Product p) {
        p.setId(null);
        return productRepository.save(p);
    }

    @Transactional
    public Product update(Long id, Product update) {
        Product existing = get(id);
        existing.setName(update.getName());
        existing.setCategory(update.getCategory());
        existing.setPriceCents(update.getPriceCents());
        existing.setStock(update.getStock());
        existing.setActive(update.getActive());
        return productRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
