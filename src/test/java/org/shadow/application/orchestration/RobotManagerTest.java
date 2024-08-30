package org.shadow.application.orchestration;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RobotManagerTest {

  @Mock private RobotScheduler scheduler1;
  @Mock private RobotScheduler scheduler2;
  @Mock private ExecutorService executorService;

  private RobotManager robotManager;

  @BeforeEach
  void setUp() {
    robotManager = new RobotManager(List.of(scheduler1, scheduler2), executorService);
  }

  @Test
  void testStart() {
    robotManager.start();

    verify(executorService, times(2)).submit(any(Runnable.class));

    verify(scheduler1, never()).start();
    verify(scheduler2, never()).start();
  }

  @Test
  void testStop() throws InterruptedException {
    when(executorService.awaitTermination(10, TimeUnit.SECONDS)).thenReturn(true);

    robotManager.stop();

    verify(scheduler1).stop();
    verify(scheduler2).stop();
    verify(executorService).shutdown();
    verify(executorService).awaitTermination(10, TimeUnit.SECONDS);
  }

  @Test
  void testStopWithTimeout() throws InterruptedException {
    when(executorService.awaitTermination(10, TimeUnit.SECONDS)).thenReturn(false);

    robotManager.stop();

    verify(scheduler1).stop();
    verify(scheduler2).stop();
    verify(executorService).shutdown();
    verify(executorService).awaitTermination(10, TimeUnit.SECONDS);
  }
}
