package org.jfw.util.execut;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ScheduleUtil {

	private ScheduleUtil() {
	}

	public static void schedule(ScheduledExecutorService service,Runnable runnable, Trigger trigger) {
		(new ReschedulingRunnable(service, runnable, trigger)).schedule();
	}

	public static ScheduledFuture<?> schedule(ScheduledExecutorService service, Runnable command, long delay) {
		return service.schedule(command, delay, TimeUnit.MILLISECONDS);
	}

	public static <V> ScheduledFuture<V> schedule(ScheduledExecutorService service, Callable<V> callable, long delay) {
		return service.schedule(callable, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleAtFixedRate(ScheduledExecutorService service, Runnable command,
			long initialDelay, long period) {
		return service.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(ScheduledExecutorService service, Runnable command,
			long initialDelay, long delay) {
		return service.scheduleWithFixedDelay(command, initialDelay, delay, TimeUnit.MILLISECONDS);
	}

	static class ReschedulingRunnable implements Runnable {
		private final Object monitor = new Object();
		private final Runnable delegate;
		private final Trigger trigger;
		private final ScheduledExecutorService service;

		ReschedulingRunnable(ScheduledExecutorService service, Runnable delegate, Trigger trigger) {
			this.service = service;
			this.delegate = delegate;
			this.trigger = trigger;
		}

		public void schedule() {
			synchronized (this.monitor) {
				long delay = this.trigger.nextExecutionTime() - System.currentTimeMillis();
				this.service.schedule(this, delay, TimeUnit.MILLISECONDS);
			}
		}

		@Override
		public void run() {
			this.delegate.run();
			this.schedule();
		}
	}
}
