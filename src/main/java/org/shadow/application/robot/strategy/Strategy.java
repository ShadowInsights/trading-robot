package org.shadow.application.robot.strategy;

import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.common.model.Position;

public interface Strategy<M> {

  M calculatePositionMomentum(List<Bar> bars);

  boolean isTimeToClosePositionInAdvance(List<Bar> bars, Position position);
}
