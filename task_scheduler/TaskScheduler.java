/**
 * 
 */
package task_scheduler;

import java.util.concurrent.TimeUnit;

import heartbeat.HeartBeat;
import timer.TimeFormatter;

/**
 * @author Steve Brown
 *
 */
public class TaskScheduler implements Schedule{
	
	private long scheduledTime = 0;
	private HeartBeat heartBeat = null;
	private TimeFormatter time = null;
	
	/*
	 *  New TaskScheduler with a starting time and heart beat to make it tick.
	 */
	public TaskScheduler(TimeFormatter time, HeartBeat heartBeat) {
		super();

		this.time = time;
		this.heartBeat = heartBeat;
	}
	
	/*
	 * (non-Javadoc)
	 * @see timer.BeatingHeart#beat()
	 */
	@Override
	public void beat() {
		heartBeat.anotherBeat();
		long currentTime = time.currentTime();
		if(scheduledTime == currentTime) {
			System.out.println("It's time");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
			beat();
	}

	/*
	 * (non-Javadoc)
	 * @see timer.Schedule#scheduleTask(int, int, int)
	 */
	@Override
	public Schedule scheduleTask(int hours, int minutes, int seconds) {
		scheduledTime = time.getTimeInSeconds(hours, minutes, seconds);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see timer.Schedule#scheduleTask(long)
	 */
	@Override
	public Schedule scheduleTask(long scheduleTime) {
		scheduledTime = time.currentTime() + scheduleTime;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see timer.Schedule#start()
	 */
	@Override
	public void start() {
		heartBeat.startBeating(this);
	}

}
