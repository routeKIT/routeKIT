package edu.kit.pse.ws2013.routekit.util;

import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

public class DummyProgressReporter extends ProgressReporter {
	public DummyProgressReporter() {
		pushTask("Dummy");
	}
}
