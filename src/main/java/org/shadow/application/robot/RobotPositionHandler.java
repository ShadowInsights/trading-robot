package org.shadow.application.robot;

public interface RobotPositionHandler<R extends Robot> {
  void handle(R robot);
}
