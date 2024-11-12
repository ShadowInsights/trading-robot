package org.shadow.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "balance")
public record BalanceEntity(
    @Id @Column(name = "id") Integer id,
    @Column(name = "amount", nullable = false, precision = 19, scale = 4) BigDecimal amount) {}
