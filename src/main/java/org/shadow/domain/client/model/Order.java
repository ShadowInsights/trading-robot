package org.shadow.domain.client.model;

import java.math.BigDecimal;
import java.time.Instant;
import org.shadow.application.robot.common.model.PositionType;

public record Order(
    long id,
    BigDecimal entry,
    Instant timestamp,
    OrderType type,
    BigDecimal amount,
    PositionType positionType,
    BigDecimal quantity) {}
