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
	 * <p>
	 * For the root task, this is called <em>after</em>
	 * {@link #startRoot(String)}.
	 * 
	 * @param name
	 *            The name of the task.
	 */
	public void beginTask(String name);

	/**
	 * Called when the progress changed.
	 * <p>
	 * When a subtask finishes, this is called <em>after</em>
	 * {@link #endTask(String)}.
	 * 
	 * @param progress
	 *            The current total progress.
	 * @param name
	 *            The name of the current task.
	 */
	public void progress(float progress, String name);

	/**
	 * Called when each task ends.
	 * <p>
	 * For the root task, this is called <em>before</em>
	 * {@link #finishRoot(String)}.
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
