package org.shadow.application.robot.strategy.model;

import java.math.BigDecimal;
import java.util.Optional;

public record BinaryPositionMomentum(
    BinaryPositionMomentumActionType momentumActionType, Optional<BigDecimal> stopLoss
    // TODO: Uncomment later, must have takeProfits as well and it must be used in all references
    //    List<BigDecimal> takeProfits
    ) {}
