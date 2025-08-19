package com.antulev.promo.model;

import com.antulev.promo.shared.ModelBase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends ModelBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private Integer priceCents;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Boolean active = true;
}
