package org.shadow.application.robot.common.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public record Position(
    PositionType type,
    BigDecimal entry,
    List<BigDecimal> takeProfits,
    Optional<BigDecimal> stopLoss) {}
