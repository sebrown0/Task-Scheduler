/**
 * 
 */
package timer;

/**
 * @author Steve Brown
 * 
 *  Holds a time specified at instantiation.
 *  The time can be incremented by 1s by calling
 */
public class Time implements TimeFormatter {

	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;
	private long currentTime = 0;
	
	/*
	 *  Construct a Time with specified H,M,S.
	 */
	public Time(int hours, int minutes, int seconds) {
		super();
		// Check the H,M,S are legal. Convert into seconds.
		this.currentTime = checkTime(hours, minutes, seconds);
		if(currentTime >= 0) {
			this.hours = hours;
			this.minutes = minutes;
			this.seconds = seconds;
		}
	}
	
	/*
	 *  Check if the specified time is legal.
	 *  An illegal parameter is set to 0. 
	 */
	private long checkTime (int hours, int minutes, int seconds) {
		seconds = (seconds >= 0 && seconds <= 60) ? seconds: 0;
		minutes = (minutes >= 0 && minutes <= 60) ? minutes: 0;
		hours = (hours >= 0 && hours <= 24) ? hours: 0;
		
		return getTimeInSeconds(hours, minutes, seconds);
	}
	
	/*
	 *  Prepend a 0 to the digit if required.
	 */
	private String formatDigit(int digit) {
		String value = String.valueOf(digit);
		
		return (digit < 10) ? "0" + value : value; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#formattedTime()
	 */
	@Override
	public String formattedTime() {
		return formatDigit(hours) + ":" + formatDigit(minutes) + ":" + formatDigit(seconds);
	}

	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#seconds()
	 */
	@Override
	public int seconds() {
		return seconds;
	}

	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#minutes()
	 */
	@Override
	public int minutes() {
		return minutes;
	}

	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#hours()
	 */
	@Override
	public int hours() {
		return hours;
	}

	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#getTimeInSeconds(int, int, int)
	 */
	@Override
	public long getTimeInSeconds(int hours, int minutes, int seconds) {
		return ((hours * 3600) + (minutes * 60) + seconds);
	}

	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#incrementTime()
	 */
	@Override
	public void incrementTime() {
		this.currentTime++;
		
		seconds++;
		if(seconds > 59) {
			seconds = 0;
			minutes++;
			if(minutes > 59) {
				minutes = 0;
				hours++;
				if(hours > 23) {
					hours = 0;
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see timer.TimeFormatter#currentTime()
	 */
	@Override
	public long currentTime() {
		return currentTime;
	}
}
