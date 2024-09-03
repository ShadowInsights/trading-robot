package org.shadow.application.robot.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.shadow.application.robot.blocker.Blocker;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.explorer.BinaryExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;

public interface Strategy<M> {

  M calculatePositionMomentum(List<Bar> bars);

  boolean isTimeToClosePositionInAdvance(List<Bar> bars, Position position);

  List<BinaryExplorer> getBinaryExplorers();

  List<Blocker> getBlockers();

  BigDecimal getStopLossRequiredPercentage();

  Map<BinaryIsMomentumExplorationState, Integer>
      getBinaryIsMomentumExplorationStateIntegerMultiplierMap();
}
