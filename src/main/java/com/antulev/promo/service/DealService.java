package com.antulev.promo.service;

import com.antulev.promo.model.Deal;
import com.antulev.promo.model.DealType;
import com.antulev.promo.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.antulev.promo.specification.DealSpecifications.*;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;

    public Page<Deal> search(
            String nameKeyword,
            DealType type,
            Long productId,
            Instant activeAt,
            Boolean active,
            Pageable pageable) {
        Specification<Deal> spec = Specification.allOf(
                nameContains(nameKeyword),
                typeEquals(type),
                forProductId(productId),
                activeAt(activeAt),
                isActive(active));
        return dealRepository.findAll(spec, pageable);
    }

    public Deal get(Long id) {
        return dealRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Deal create(Deal d) {
        d.setId(null);
        return dealRepository.save(d);
    }

    @Transactional
    public Deal update(Long id, Deal update) {
        Deal existing = get(id);
        existing.setName(update.getName());
        existing.setType(update.getType());
        existing.setProduct(update.getProduct());
        existing.setBuyQty(update.getBuyQty());
        existing.setGetQty(update.getGetQty());
        existing.setPercentOff(update.getPercentOff());
        existing.setStartsAt(update.getStartsAt());
        existing.setExpiresAt(update.getExpiresAt());
        existing.setActive(update.getActive());
        return dealRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        dealRepository.deleteById(id);
    }
}
