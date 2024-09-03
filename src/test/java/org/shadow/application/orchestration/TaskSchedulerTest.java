package org.shadow.application.orchestration;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskSchedulerTest {

  @Mock private ScheduledExecutorService scheduler;
  @Mock private Runnable task;
  @Mock private ScheduledFuture<?> scheduledFuture; // Specify the wildcard type

  private TaskScheduler taskScheduler;

  @BeforeEach
  void setUp() {
    taskScheduler = new TaskScheduler(scheduler);
  }

  @Test
  void testStart() {
    var initialDelay = 1000L;
    var intervalMillis = 5000L;

    when(scheduler.scheduleAtFixedRate(
            any(Runnable.class), eq(initialDelay), eq(intervalMillis), eq(TimeUnit.MILLISECONDS)))
        .thenReturn(mock(ScheduledFuture.class));

    taskScheduler.start(task, initialDelay, intervalMillis);

    ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
    verify(scheduler)
        .scheduleAtFixedRate(
            captor.capture(), eq(initialDelay), eq(intervalMillis), eq(TimeUnit.MILLISECONDS));

    captor.getValue().run();
    verify(task, times(1)).run();
  }

  @Test
  void testStop() throws IllegalAccessException, NoSuchFieldException {
    when(scheduler.scheduleAtFixedRate(
            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
        .thenReturn(mock(ScheduledFuture.class));

    taskScheduler.start(task, 1000L, 5000L);

    var scheduledFutureField = TaskScheduler.class.getDeclaredField("scheduledFuture");
    scheduledFutureField.setAccessible(true);
    scheduledFutureField.set(taskScheduler, scheduledFuture);

    taskScheduler.stop();

    verify(scheduler).shutdown();
    verify(scheduledFuture).cancel(true); // Verify that cancel(true) was called
  }

  @Test
  void testStopWhenAwaitTerminationFalse() throws InterruptedException {
    when(scheduler.scheduleAtFixedRate(
            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
        .thenReturn(mock(ScheduledFuture.class));
    when(scheduler.awaitTermination(10, TimeUnit.SECONDS)).thenReturn(false);

    taskScheduler.start(task, 1000L, 5000L);
    taskScheduler.stop();

    verify(scheduler).shutdown();
    verify(scheduler).shutdownNow();
  }

  @Test
  void testStopWhenAwaitTerminationRaisedException() throws InterruptedException {
    when(scheduler.scheduleAtFixedRate(
            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
        .thenReturn(mock(ScheduledFuture.class));
    when(scheduler.awaitTermination(10, TimeUnit.SECONDS)).thenThrow(InterruptedException.class);

    taskScheduler.start(task, 1000L, 5000L);
    taskScheduler.stop();

    verify(scheduler).shutdown();
    verify(scheduler).shutdownNow();
  }

  @Test
  void testTaskExecutionWhenStopped() {
    var initialDelay = 1000L;
    var intervalMillis = 5000L;

    when(scheduler.scheduleAtFixedRate(
            any(Runnable.class), eq(initialDelay), eq(intervalMillis), eq(TimeUnit.MILLISECONDS)))
        .thenReturn(mock(ScheduledFuture.class));

    taskScheduler.start(task, initialDelay, intervalMillis);
    taskScheduler.stop();

    var captor = ArgumentCaptor.forClass(Runnable.class);
    verify(scheduler)
        .scheduleAtFixedRate(
            captor.capture(), eq(initialDelay), eq(intervalMillis), eq(TimeUnit.MILLISECONDS));

    captor.getValue().run();

    verify(task, never()).run();
  }

  @Test
  void testStopBeforeTaskRuns() throws Exception {
    when(scheduler.scheduleAtFixedRate(
            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
        .thenReturn(mock(ScheduledFuture.class));

    taskScheduler.start(task, 1000L, 5000L);

    var scheduledFutureField = TaskScheduler.class.getDeclaredField("scheduledFuture");
    scheduledFutureField.setAccessible(true);
    scheduledFutureField.set(taskScheduler, scheduledFuture);

    taskScheduler.stop();

    verify(scheduler).shutdown();
    verify(scheduledFuture).cancel(true);
    verify(task, never()).run();
  }
}
