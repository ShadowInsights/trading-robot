package org.shadow.application.robot.common.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Bar(
    Instant time,
    BigDecimal open,
    BigDecimal high,
    BigDecimal low,
    BigDecimal close,
    BigDecimal volume) {}
