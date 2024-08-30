package org.shadow.application.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FakeRobot implements Robot {

  private static final Logger logger = LogManager.getLogger(FakeRobot.class);

  @Override
  public void run() {
    logger.info("FakeRobot has started running.");
  }

  @Override
  public void stop() {
    logger.info("FakeRobot has been stopped.");
  }
}
