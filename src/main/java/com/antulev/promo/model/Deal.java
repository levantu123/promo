package com.antulev.promo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import com.antulev.promo.shared.ModelBase;

@Entity
@Table(name = "deals")
@Getter
@Setter
public class Deal extends ModelBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DealType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer buyQty = 1;
    @Column(nullable = false)
    private Integer getQty = 1;
    @Column(nullable = false)
    private Integer percentOff;

    private Instant startsAt;
    private Instant expiresAt;
    @Column(nullable = false)
    private Boolean active = true;
}
