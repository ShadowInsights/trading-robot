package org.shadow.application.robot.explorer;

import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.MACDIndicator;

import java.util.List;

public class MACDBinaryExplorer implements BinaryExplorer {

    private final Integer severity;
    private final MACDIndicator macdIndicator;

    public MACDBinaryExplorer(Integer severity, Integer shortPeriod, Integer longPeriod, Integer signalPeriod) {
        this.severity = severity;
        this.macdIndicator = new MACDIndicator(12, 26, 9);
    }

    @Override
    public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {



        return null;
    }

    @Override
    public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
        return null;
    }

    @Override
    public Integer getSeverity() {
        return severity;
    }
}
