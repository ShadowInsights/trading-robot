package org.shadow.application.robot;

import java.math.BigDecimal;
import java.util.List;
import org.shadow.application.robot.common.model.Position;

public interface Robot extends Runnable {

  void init();

  void stop();

  RobotTimeframe getRobotTimeframe();

  String getSymbol();

  List<Position> getPositions();

  BigDecimal getPercentageFromDeposit();

  Integer getOrderFuturesMultiplier();
}
