package org.shadow.application.robot.explorer;

import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;

// TODO: Complete implementation
public class RSIBinaryExplorer implements BinaryExplorer {

  private final Integer severity;

  public RSIBinaryExplorer(Integer severity) {
    this.severity = severity;
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  @Override
  public Integer getSeverity() {
    return severity;
  }
}
