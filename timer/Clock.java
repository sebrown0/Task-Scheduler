/**
 * 
 */
package timer;

import heartbeat.HeartBeat;

/**
 * @author Steve Brown
 *
 *  A clock that runs with a HeartBeat.
 *  The clock ticks forward with an interval specified by the HeartBeat.
 */
public class Clock implements Timer{
	
	private HeartBeat heartBeat = null;
	private TimeFormatter time = null;
	
	/*
	 *  New clock with a starting time and heart beat to make it tick.
	 */
	public Clock(TimeFormatter time, HeartBeat heartBeat) {
		this.time = time;
		this.heartBeat = heartBeat;
	}
	
	/*
	 * (non-Javadoc)
	 * @see timer.BeatingHeart#beat()
	 */
	@Override
	public void beat() {
		heartBeat.anotherBeat();  				// Increment the number of heart beats.
		time.incrementTime();					// Add 1s to the time.
		System.out.println(time.currentTime());	// TODO - R
	}
	
	/*
	 * (non-Javadoc)
	 * @see timer.Timer#startClock()
	 */
	@Override
	public void start() {
		System.out.println("Clock started");
		heartBeat.startBeating(this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
			beat();
	}
	
}
