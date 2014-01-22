package edu.kit.pse.ws2013.routekit.models;

/**
 * Listens for progress.
 * 
 * @author Lucas Werkmeister
 * @see ProgressReporter
 */
public interface ProgressListener {
	/**
	 * Called when the root task starts.
	 * 
	 * @param name
	 *            The name of the root task.
	 */
	public void startRoot(String name);

	/**
	 * Called when each task starts.
	 * 
	 * @param name
	 *            The name of the task.
	 */
	public void beginTask(String name);

	/**
	 * Called when the progress changed.
	 * 
	 * @param progress
	 *            The current total progress.
	 * @param name
	 *            The name of the current task.
	 */
	public void progress(float progress, String name);

	/**
	 * Called when each task ends.
	 * 
	 * @param name
	 *            The name of the task.
	 */
	public void endTask(String name);

	/**
	 * Called when the root task finishes.
	 * 
	 * @param name
	 *            The name of the root task.
	 */
	public void finishRoot(String name);
}
