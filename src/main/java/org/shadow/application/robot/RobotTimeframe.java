package org.shadow.application.robot;

import java.util.concurrent.TimeUnit;

public record RobotTimeframe(TimeUnit unit, long interval) {}
