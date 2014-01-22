package edu.kit.pse.ws2013.routekit.util;

import edu.kit.pse.ws2013.routekit.models.ProgressListener;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

public class DummyProgressReporter extends ProgressReporter {

	@Override
	public void pushTask(String name) {
	}

	@Override
	public void setSubTasks(float[] weights) {
	}

	@Override
	public void setSubTasks(int count) {
	}

	@Override
	public void popTask(String name) {
	}

	@Override
	public void popTask() {
	}

	@Override
	public void addProgressListener(ProgressListener listener) {
	}

	@Override
	public float getProgress() {
		return 1f;
	}

	@Override
	public String getCurrentTask() {
		return "Dummy";
	}
}
