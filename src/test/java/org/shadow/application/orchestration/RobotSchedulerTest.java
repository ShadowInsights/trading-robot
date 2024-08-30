package org.shadow.application.orchestration;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shadow.application.robot.FakeRobot;

@ExtendWith(MockitoExtension.class)
class RobotSchedulerTest {

  @Mock private FakeRobot robot;
  @Mock private TaskScheduler taskScheduler;

  private RobotScheduler robotScheduler;

  @BeforeEach
  void setUp() {
    robotScheduler = new RobotScheduler(robot, 5, taskScheduler);
  }

  @Test
  void testStart() {
    robotScheduler.start();

    var captor = ArgumentCaptor.forClass(Runnable.class);
    verify(taskScheduler).start(captor.capture(), eq(5));

    captor.getValue().run();
    verify(robot).run();
  }

  @Test
  void testStop() {
    robotScheduler.stop();

    verify(taskScheduler).stop();
  }
}
