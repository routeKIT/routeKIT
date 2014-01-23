package edu.kit.pse.ws2013.routekit.models;

import java.io.Closeable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Reports progress to attached {@link ProgressListener listeners}.
 * <p>
 * The progress of a task can be determined in one of two ways:
 * <ul>
 * <li><em>Subtask-based:</em> You can {@link #setSubTasks(float[]) tell} a task
 * in advance how many sub-tasks will be {@link #pushTask(String) pushed} onto
 * and {@link #popTask() popped} off of it, and how their individual progress
 * should be weighted.</li>
 * <li><em>Progress-based:</em> Alternatively, you can directly {@link
 * setProgress(float) set} its progress.</li>
 * </ul>
 * These approaches may not be combined.
 * 
 * @author Lucas Werkmeister
 */
public class ProgressReporter {
	private final Set<ProgressListener> listeners = new HashSet<>();
	private final LinkedList<Task> taskStack = new LinkedList<>();

	/**
	 * Creates a new {@link ProgressReporter}.
	 */
	public ProgressReporter() {
	}

	/**
	 * Begin a new task with the specified name.
	 * <p>
	 * A call to {@link #setSubTasks(float[])} should usually occur directly
	 * after a call to this method unless you want to
	 * {@link #setProgress(float) set the progress directly}; the two methods
	 * are separate because this method is usually called before entering a
	 * subroutine while only the subroutine can know about its subtasks (which
	 * are internal by nature).
	 * 
	 * @param name
	 *            The name of the task.
	 */
	public void pushTask(String name) {
		Task newTask = new Task(name);
		if (taskStack.isEmpty()) {
			reportStartRoot(name);
		} else {
			taskStack.getLast().addTask(newTask);
		}
		reportBeginTask(name);
		taskStack.addLast(newTask);
	}

	/**
	 * Set the weights of subtasks of this task.
	 * <p>
	 * This should be called directly after a call to {@link #pushTask(String)};
	 * for rationale see there.
	 * <p>
	 * This is only permitted if the progress hasn’t been set directly; see the
	 * class documentation for more information.
	 * 
	 * @param weights
	 *            The weights of the subtasks, which should all be in range
	 *            [0,1] and sum up to 1 (but this is not checked).
	 * @throws IllegalStateException
	 *             If progress has been set.
	 */
	public void setSubTasks(float[] weights) {
		if (taskStack.getLast().progress != -1) {
			throw new IllegalStateException(
					"Can’t set subtasks of a task with direct progress!");
		}
		taskStack.getLast().setSubTasks(weights);
	}

	/**
	 * Set the amount of subtasks of this task; they all have the same weight.
	 * 
	 * @param count
	 *            The number of subtasks.
	 * @see #setSubTasks(float[])
	 */
	public void setSubTasks(int count) {
		float[] weights = new float[count];
		Arrays.fill(weights, 1f / count);
		setSubTasks(weights);
	}

	/**
	 * End the current task, verifying its name.
	 * <p>
	 * If there are unpopped tasks on top of a task with the given name, they
	 * are popped as well. This can be used for error recovery: if you call this
	 * method from a {@code finally} block, progress reporting will not be
	 * disrupted even if some sub-task throws an exception.
	 * <p>
	 * If no task with the given name exists, an {@link AssertionError} is
	 * thrown.
	 * 
	 * @param name
	 *            The expected name of the task to end.
	 * @throws AssertionError
	 *             If no task with the given name exists.
	 * @see #popTask()
	 */
	public void popTask(String name) {
		boolean hasTask = false;
		for (Task task : taskStack) {
			if (task.name.equals(name)) {
				hasTask = true;
				break;
			}
		}
		if (!hasTask) {
			throw new AssertionError();
		}
		boolean poppedTask = false;
		while (!poppedTask) {
			if (taskStack.getLast().name.equals(name)) {
				poppedTask = true;
			}
			popTask();
		}
	}

	/**
	 * End the current task.
	 */
	public void popTask() {
		String name = taskStack.getLast().name;
		taskStack.getLast().finish();
		taskStack.removeLast();
		reportEndTask(name);
		reportProgress();
		if (taskStack.isEmpty()) {
			reportFinishRoot(name);
		}
	}

	/**
	 * End the current task and begin a new task with the specified name.
	 * <p>
	 * This is a convenience method, equivalent to calling
	 * 
	 * <pre>
	 * {@code
	 * popTask();
	 * pushTask(name);
	 * }
	 * </pre>
	 * 
	 * @param name
	 *            The name of the new task.
	 */
	public void nextTask(String name) {
		popTask();
		pushTask(name);
	}

	/**
	 * Start a new task with the given name and end it when the returned object
	 * is {@link CloseableTask#close() closed}.
	 * <p>
	 * This is a convenience method, intended for use in try-with-resources
	 * statements.
	 * <p>
	 * For ending the task, the {@link #popTask(String)} variant is used, which
	 * means that when you use this method in a try-with-resources statement,
	 * exceptions thrown in sub-tasks do not disrupt progress reporting.
	 * 
	 * @param name
	 *            The name of the new task.
	 * @return A {@link CloseableTask} whose {@link Closeable#close() close()}
	 *         method closes the task again.
	 */
	public CloseableTask openTask(final String name) {
		pushTask(name);
		return new CloseableTask(name);
	}

	public class CloseableTask implements AutoCloseable {
		private final String name;

		private CloseableTask(String name) {
			this.name = name;
		}

		@Override
		public void close() {
			popTask(name);
		}
	}

	/**
	 * Add a {@link ProgressListener} to this reporter.
	 * 
	 * @param listener
	 *            The new {@link ProgressListener}.
	 */
	public void addProgressListener(ProgressListener listener) {
		listeners.add(listener);
	}

	/**
	 * Sets the progress of the current task.
	 * <p>
	 * This is only permitted if no subtasks have been set; see the class
	 * documentation for more information.
	 * 
	 * @param progress
	 *            The progress.
	 * @throws IllegalStateException
	 *             If subtasks have been set.
	 */
	public void setProgress(float progress) {
		if (!taskStack.getLast().subTasks.isEmpty()) {
			throw new IllegalStateException(
					"Can’t set the progress of a task with subtasks!");
		}
		taskStack.getLast().progress = progress;
		reportProgress();
	}

	/**
	 * Gets the current progress.
	 * 
	 * @return The current progress.
	 */
	public float getProgress() {
		if (taskStack.isEmpty()) {
			return 1f;
		}
		return taskStack.getFirst().getProgress();
	}

	/**
	 * Gets the current task.
	 * 
	 * @return The current task.
	 */
	public String getCurrentTask() {
		if (taskStack.isEmpty()) {
			return null;
		}
		return taskStack.getLast().name;
	}

	private void reportStartRoot(String name) {
		for (ProgressListener listener : listeners) {
			listener.startRoot(name);
		}
	}

	private void reportBeginTask(String name) {
		for (ProgressListener listener : listeners) {
			listener.beginTask(name);
		}
	}

	private void reportProgress() {
		float progress = getProgress();
		String task = getCurrentTask();
		for (ProgressListener listener : listeners) {
			listener.progress(progress, task);
		}
	}

	private void reportEndTask(String name) {
		for (ProgressListener listener : listeners) {
			listener.endTask(name);
		}
	}

	private void reportFinishRoot(String name) {
		for (ProgressListener listener : listeners) {
			listener.finishRoot(name);
		}
	}

	private static class Task {
		public final String name;
		private float[] weights;
		private final LinkedList<Task> subTasks = new LinkedList<>();
		private boolean finished = false;
		private float progress = -1;

		public Task(String name) {
			this.name = name;
		}

		public void setSubTasks(float[] weights) {
			if (weights == null) {
				throw new NullPointerException();
			}
			this.weights = weights;
		}

		public void addTask(Task subTask) {
			subTasks.add(subTask);
		}

		public float getProgress() {
			if (finished) {
				return 1f;
			}
			if (subTasks.isEmpty()) {
				return progress;
			}
			float progress = 0f;
			int i = 0;
			for (Task subTask : subTasks) {
				float weight = i < weights.length ? weights[i] : 0f;
				progress += weight * subTask.getProgress();
				i++;
			}
			return progress;
		}

		public void finish() {
			finished = true;
		}
	}
}
