package utility;

/**
 * @author Alex Adams (s1046358)
 */
public class SafeSleep {
	/**
	 * A thread-safe sleep method which will release implicit locks held by the
	 * calling thread. Performance is otherwise identical to Thread.sleep(...)
	 * 
	 * @param millis
	 *            The time to sleep for in milliseconds
	 */
	public static void sleep(long millis) throws InterruptedException {
		if (millis == 0)
			return;
		Object waiter = new Object();
		synchronized (waiter) {
			long timeBefore = System.currentTimeMillis();
			long timePassed = 0;
			// Prevent spurious wake-ups from causing early return
			do {
				assert (millis - timePassed > 0) : "SafeSleep tried to sleep for non-positive time";
				waiter.wait(millis - timePassed);
				timePassed = System.currentTimeMillis() - timeBefore;
			} while (timePassed < millis);
		}
	}
}
