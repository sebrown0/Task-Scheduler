/**
 * 
 */
package timer;

/**
 * @author Brown
 *
 */
public interface TimeFormatter {
	
	/*
	 *  Increment time by 1s.
	 */
	void incrementTime(); 
	
	/*
	 *  Get the time in seconds for the given H, M, S.
	 */
	long getTimeInSeconds(int hours, int minutes, int seconds);
	
	/*
	 *  The current time in seconds.
	 */
	long currentTime(); 	
	
	/*
	 *  Get the time in the format hh:mm:ss.
	 */
	String formattedTime();

	/*
	 *  Get the current second.
	 */
	int seconds();
	
	/*
	 *  Get the current minute.
	 */
	int minutes();
	
	/*
	 *  Get the current hour.
	 */
	int hours();

}
