package edu.kit.pse.ws2013.routekit.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Reports progress to attached {@link ProgressListener listeners}.
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
	 * after a call to this method; the two methods have been separated because
	 * this method is usually called before entering a subroutine while only the
	 * subroutine can know about its subtasks (which are internal by nature).
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
	 * 
	 * @param weights
	 *            The weights of the subtasks, which should all be in range
	 *            [0,1] and sum up to 1 (but this is not checked).
	 */
	public void setSubTasks(float[] weights) {
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
	 * Add a {@link ProgressListener} to this reporter.
	 * 
	 * @param listener
	 *            The new {@link ProgressListener}.
	 */
	public void addProgressListener(ProgressListener listener) {
		listeners.add(listener);
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
