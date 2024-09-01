// package org.shadow.application.orchestration;
//
// import static org.mockito.Mockito.*;
//
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.ScheduledFuture;
// import java.util.concurrent.TimeUnit;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class TaskSchedulerTest {
//
//  @Mock private ScheduledExecutorService scheduler;
//
//  @Mock private Runnable task;
//
//  private TaskScheduler taskScheduler;
//
//  @BeforeEach
//  void setUp() {
//    taskScheduler = new TaskScheduler(scheduler);
//  }
//
//  @Test
//  void testStart() {
//    var intervalSeconds = 5;
//
//    when(scheduler.scheduleAtFixedRate(
//            any(Runnable.class), eq(0L), eq((long) intervalSeconds), eq(TimeUnit.SECONDS)))
//        .thenReturn(mock(ScheduledFuture.class));
//
//    taskScheduler.start(task, intervalSeconds);
//
//    ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
//    verify(scheduler)
//        .scheduleAtFixedRate(
//            captor.capture(), eq(0L), eq((long) intervalSeconds), eq(TimeUnit.SECONDS));
//
//    captor.getValue().run();
//    verify(task, times(1)).run();
//  }
//
//  @Test
//  void testStop() {
//    when(scheduler.scheduleAtFixedRate(
//            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
//        .thenReturn(mock(ScheduledFuture.class));
//
//    taskScheduler.start(task, 5);
//    taskScheduler.stop();
//
//    verify(scheduler).shutdown();
//  }
//
//  @Test
//  void testStopWhenAwaitTerminationFalse() throws InterruptedException {
//    when(scheduler.scheduleAtFixedRate(
//            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
//        .thenReturn(mock(ScheduledFuture.class));
//    when(scheduler.awaitTermination(10, TimeUnit.SECONDS)).thenReturn(false);
//
//    taskScheduler.start(task, 5);
//    taskScheduler.stop();
//
//    verify(scheduler).shutdown();
//    verify(scheduler).shutdownNow();
//  }
//
//  @Test
//  void testStopWhenAwaitTerminationRaisedException() throws InterruptedException {
//    when(scheduler.scheduleAtFixedRate(
//            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
//        .thenReturn(mock(ScheduledFuture.class));
//    when(scheduler.awaitTermination(10, TimeUnit.SECONDS)).thenThrow(InterruptedException.class);
//
//    taskScheduler.start(task, 5);
//    taskScheduler.stop();
//
//    verify(scheduler).shutdown();
//    verify(scheduler).shutdownNow();
//  }
//
//  @Test
//  void testTaskExecutionWhenStopped() {
//    var intervalSeconds = 5;
//
//    when(scheduler.scheduleAtFixedRate(
//            any(Runnable.class), eq(0L), eq((long) intervalSeconds), eq(TimeUnit.SECONDS)))
//        .thenReturn(mock(ScheduledFuture.class));
//
//    taskScheduler.start(task, intervalSeconds);
//    taskScheduler.stop();
//
//    var captor = ArgumentCaptor.forClass(Runnable.class);
//    verify(scheduler)
//        .scheduleAtFixedRate(
//            captor.capture(), eq(0L), eq((long) intervalSeconds), eq(TimeUnit.SECONDS));
//
//    captor.getValue().run();
//
//    verify(task, never()).run();
//  }
//
//  @Test
//  void testStopBeforeTaskRuns() {
//    var scheduledFuture = mock(ScheduledFuture.class);
//    when(scheduler.scheduleAtFixedRate(
//            any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
//        .thenReturn(scheduledFuture);
//
//    taskScheduler.start(task, 5);
//    taskScheduler.stop();
//
//    verify(scheduler).shutdown();
//    verify(scheduledFuture).cancel(true);
//    verify(task, never()).run();
//  }
// }
