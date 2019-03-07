/**
 * 
 */
package timer;

import heartbeat.BeatingHeart;

/**
 * @author Steve Brown
 *
 *  Interface for a timer.
 */
public interface Timer extends BeatingHeart {
	
	/*
	 *  Start the timer.
	 */
	void start();
}
