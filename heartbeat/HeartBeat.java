/**
 * 
 */
package heartbeat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.Task;

/**
 * @author Steve Brown
 *
 *  
 */
public class HeartBeat implements BeatingHeart {

	private ScheduledExecutorService heartBeat = Executors.newSingleThreadScheduledExecutor();
	
	private long numberOfBeats = 0;
	private long maxNumberOfBeats = Long.MAX_VALUE - 1;

	private int beat = 1;
	private TimeUnit timeUnit = TimeUnit.SECONDS;
	
	/*
	 *  New heart beat with delay between beats and unit of delay.
	 */
	public HeartBeat(int beatDuration, TimeUnit timeUnit) {
		this.beat = beatDuration;
		this.timeUnit = timeUnit;
	}
	
	/*
	 *  Execute the Beatable target after beat beats.
	 *  Keeps going until told to shut down (stopBeating).
	 */
	public void startBeating(BeatingHeart target) {	
		heartBeat.scheduleAtFixedRate(target, beat, beat, timeUnit);
	}
	
	/*
	 *  Execute the Beatable target after beat beats.
	 *  Keeps going until told to shut down (stopBeating) or maxNumberOfBeats are reached.
	 */
	public void startBeating(BeatingHeart target, long maxNumberOfBeats) {	
		this.maxNumberOfBeats = maxNumberOfBeats;
		heartBeat.scheduleAtFixedRate(target, beat, beat,  timeUnit);
	}
	
	/*
	 *  Increment the number of beats.
	 */
	public void anotherBeat() {
//		System.out.println("Beat number: " + numberOfBeats); // TODO - Remove
		numberOfBeats++;
		if(numberOfBeats >= maxNumberOfBeats) {
			stopBeating();
		}
	}

	/*
	 *  Shutdown the heart beat and therefore the target. 
	 */
	public void stopBeating() {
		System.out.println("Stopping");
		try {
			heartBeat.awaitTermination(beat, timeUnit);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		heartBeat.shutdown();
	}

	
	@Override
	public void run() {
		// Beatable object should implement.
	}
	
	@Override
	public void beat() {
		// Beatable object should implement.		
	}
	
}
	
