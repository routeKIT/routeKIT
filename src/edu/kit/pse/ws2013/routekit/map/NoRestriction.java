package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

public class NoRestriction extends Restriction {
	private static final NoRestriction instance = new NoRestriction();

	private NoRestriction() {
	}

	public static Restriction getInstance() {
		return instance;
	}

	@Override
	public boolean allows(Profile profile) {
		return true;
	}

	@Override
	protected void saveInternal(DataOutput out) throws IOException {
		// do nothing
	}

	protected static Restriction loadInternal(DataInput in) {
		return getInstance();
	}
}
