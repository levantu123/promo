package com.antulev.promo.model;

import com.antulev.promo.shared.ModelBase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "basket_items", uniqueConstraints = @UniqueConstraint(name = "uk_basket_product", columnNames = {
        "basket_id", "product_id" }))
@Getter
@Setter
public class BasketItem extends ModelBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "basket_id", nullable = false)
    private Basket basket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Integer unitPriceCents;
}
