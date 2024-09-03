package org.shadow.application.orchestration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shadow.application.orchestration.util.TimeUtil;
import org.shadow.application.robot.Robot;
import org.shadow.application.robot.RobotTimeframe;

@ExtendWith(MockitoExtension.class)
class RobotSchedulerTest {

  @Mock private Robot robot;
  @Mock private TaskScheduler taskScheduler;

  private RobotScheduler robotScheduler;

  @BeforeEach
  void setUp() {
    robotScheduler = new RobotScheduler(robot, taskScheduler);
  }

  @Test
  void testStart() {
    when(robot.getSymbol()).thenReturn("TEST");
    when(robot.getRobotTimeframe()).thenReturn(new RobotTimeframe(TimeUnit.SECONDS, 5));

    try (var utilities = mockStatic(TimeUtil.class)) {
      utilities
          .when(() -> TimeUtil.calculateInitialDelayUntilNextPeriod(anyLong(), any(TimeUnit.class)))
          .thenReturn(5000L);

      robotScheduler.start();

      var captorRunnable = ArgumentCaptor.forClass(Runnable.class);
      var captorInitialDelay = ArgumentCaptor.forClass(Long.class);
      var captorInterval = ArgumentCaptor.forClass(Long.class);

      verify(taskScheduler)
          .start(captorRunnable.capture(), captorInitialDelay.capture(), captorInterval.capture());

      assertNotNull(captorRunnable.getValue());
      assertEquals(5000L, captorInitialDelay.getValue());
      assertEquals(5000L, captorInterval.getValue());

      captorRunnable.getValue().run();
      verify(robot).run();
    }
  }

  @Test
  void testStop() {
    robotScheduler.stop();

    verify(robot).stop();
    verify(taskScheduler).stop();
  }
}
