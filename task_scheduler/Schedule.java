/**
 * 
 */
package task_scheduler;

import heartbeat.BeatingHeart;

/**
 * @author Steve Brown
 *
 */
public interface Schedule extends BeatingHeart {
	
	/*
	 *  Schedule a task for a specific time.
	 */
	Schedule scheduleTask(int hours, int minutes, int seconds);
	
	/*
	 *  Schedule a task for a specific time period.
	 */
	Schedule scheduleTask(long time);
	
	/*
	 * TODO
	 */
	void start();
	
//	String getTime();
}
