package org.shadow.application.robot.explorer;

import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.Indicator;

public interface BinaryExplorer {

  BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars);

  BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars);

  Integer getSeverity();

  Indicator getIndicator();
}
