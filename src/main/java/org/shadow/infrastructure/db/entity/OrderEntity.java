package org.shadow.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.domain.client.model.OrderType;

@Entity
@Table(name = "orders")
public record OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id,
    @Column(name = "entry_price", nullable = false, precision = 19, scale = 4)
        BigDecimal entryPrice,
    @Column(name = "amount", nullable = false, precision = 19, scale = 4) BigDecimal amount,
    @Column(name = "position_type", nullable = false) @Enumerated(EnumType.STRING)
        PositionType positionType,
    @Column(name = "quantity", nullable = false, precision = 19, scale = 10) BigDecimal quantity,
    @Column(name = "order_type", nullable = false) @Enumerated(EnumType.STRING) OrderType orderType,
    @Column(name = "timestamp", nullable = false) Instant timestamp,
    @Column(name = "status", nullable = false) String status,
    @Column(name = "exit_price", precision = 19, scale = 4) BigDecimal exitPrice,
    @Column(name = "profit_loss", precision = 19, scale = 4) BigDecimal profitOrLoss,
    @Column(name = "close_timestamp") Instant closeTimestamp) {}

// TODO: Add balance id