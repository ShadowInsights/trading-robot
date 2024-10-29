package org.shadow.application.robot.blocker;

import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.indicator.Indicator;

public interface Blocker {

  boolean isMomentumToBlocking(List<Bar> bars);

  Indicator getIndicator();
}
