/**
 * 
 */
package heartbeat;

/**
 * @author Steve Brown
 *
 *  An object that has a heart beat.
 */
public interface BeatingHeart extends Runnable {

	/*
	 *  Should be over ridden by any object that implements BeatingHeart.
	 *  And called from the run of the implementing object.
	 *  
	 *  Not strictly needed as the logic could be implemented in run().
	 *  However, this represents the Heart Beat. 
	 */
	void beat();
		
}
