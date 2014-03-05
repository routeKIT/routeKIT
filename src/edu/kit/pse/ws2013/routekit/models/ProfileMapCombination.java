package edu.kit.pse.ws2013.routekit.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A combination of a {@link Profile} and a {@link StreetMap}, optionally with
 * results of a pre-calculation for them.
 */
public class ProfileMapCombination {
	private final StreetMap map;
	private final Profile profile;
	protected Weights weights;
	protected ArcFlags arcFlags;
	protected int calculationTime;

	/**
	 * Creates a new {@code ProfileMapCombination} of the given street map and
	 * profile without a precalculation.
	 * 
	 * @param map
	 *            the {@link StreetMap}
	 * @param profile
	 *            the {@link Profile}
	 */
	public ProfileMapCombination(StreetMap map, Profile profile) {
		this.map = map;
		this.profile = profile;
	}

	/**
	 * Creates a new {@code ProfileMapCombination} of the given street map and
	 * profile with a precalculation.
	 * 
	 * @param map
	 *            the {@link StreetMap}
	 * @param profile
	 *            the {@link Profile}
	 * @param weights
	 *            the calculated {@link Weights}
	 * @param arcFlags
	 *            the calculated {@link ArcFlags}
	 * @param calculationTime
	 *            the time needed for the precalculation
	 */
	public ProfileMapCombination(StreetMap map, Profile profile,
			Weights weights, ArcFlags arcFlags, int calculationTime) {
		this(map, profile);
		this.weights = weights;
		this.arcFlags = arcFlags;
		this.calculationTime = calculationTime;
	}

	/**
	 * Returns if there is a precalculation for this
	 * {@link ProfileMapCombination}, that is, if there are {@link Weights} and
	 * {@link ArcFlags}.
	 * 
	 * @return {@code true} if there is a precalculation, {@code false}
	 *         otherwise.
	 */
	public boolean isCalculated() {
		return weights != null && arcFlags != null;
	}

	/**
	 * Returns the {@link StreetMap} belonging to this combination.
	 * 
	 * @return the street map
	 */
	public StreetMap getStreetMap() {
		return map;
	}

	/**
	 * Returns the {@link Profile} belonging to this combination.
	 * 
	 * @return the profile
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * Returns the edge weights calculated for this
	 * {@code ProfileMapCombination}.
	 * 
	 * @return the {@link Weights}
	 * @throws IllegalStateException
	 *             if the weights have not been calculated yet
	 */
	public Weights getWeights() {
		if (weights == null) {
			throw new IllegalStateException();
		}
		return weights;
	}

	/**
	 * Returns the {@link ArcFlags} calculated for this combination.
	 * 
	 * @return the arc flags
	 * @throws IllegalStateException
	 *             if there is no precalculation for this combination
	 */
	public ArcFlags getArcFlags() {
		if (!isCalculated()) {
			throw new IllegalStateException();
		}
		return arcFlags;
	}

	/**
	 * @deprecated Use {@link #setWeights(Weights, int)} instead.
	 */
	@Deprecated
	public void setWeights(Weights weights) {
		this.weights = weights;
	}

	/**
	 * Sets the edge weights for this {@code ProfileMapCombination}.
	 * 
	 * @param weights
	 *            the calculated {@link Weights}
	 * @param calculationTime
	 *            the time needed for calculating the weights
	 */
	public void setWeights(Weights weights, int calculationTime) {
		this.weights = weights;
		assert (calculationTime > 0);
		this.calculationTime += calculationTime;
	}

	/**
	 * @deprecated Use {@link #setArcFlags(ArcFlags, int)} instead.
	 */
	@Deprecated
	public void setArcFlags(ArcFlags arcFlags) {
		this.arcFlags = arcFlags;
	}

	/**
	 * Sets the {@link ArcFlags} for this combination.
	 * 
	 * @param arcFlags
	 *            the calculated {@link ArcFlags}
	 * @param calculationTime
	 *            the time needed for calculating the flags
	 */
	public void setArcFlags(ArcFlags arcFlags, int calculationTime) {
		this.arcFlags = arcFlags;
		assert (calculationTime >= 0);
		this.calculationTime += calculationTime;
	}

	/**
	 * The time that the precalculation of this {@link ProfileMapCombination}
	 * took, in milliseconds. For not yet precalculated combinations, this is
	 * {@code 0}.
	 * 
	 * @return The calculation time of this combination, in milliseconds.
	 */
	public int getCalculationTime() {
		return calculationTime;
	}

	/**
	 * Ensures that this {@link ProfileMapCombination} / precalculation is
	 * completely loaded. This is a noop for normal combinations.
	 * 
	 * @param reporter
	 *            A {@link ProgressReporter} to report loading progress to.
	 * @see #loadLazily(Profile, StreetMap, File)
	 */
	public void ensureLoaded(ProgressReporter reporter) {
		map.ensureLoaded(reporter);
	}

	@Override
	public String toString() {
		return saveFileName(profile, map);
	}

	private static String saveFileName(Profile profile, StreetMap map) {
		return map.getName() + " + " + profile.getName();
	}

	/**
	 * Saves the {@link ProfileMapCombination} to the given directory by saving
	 * the {@link #getWeights() Weights}, the {@link #getArcFlags() ArcFlags}
	 * and the {@link #getCalculationTime() calculation time} to the files
	 * {@code &lt;name&gt;.weights}, {@code &lt;name&gt;.arcflags} and
	 * {@code &lt;name&gt;.time} (where {@code &lt;name&gt;} is the name of the
	 * map " + " the name of the profile).
	 * 
	 * @param directory
	 *            The directory to which the {@link ProfileMapCombination}
	 *            should be saved.
	 * @throws IOException
	 *             If the files can’t be written.
	 * @see #load(File)
	 */
	public void save(File directory) throws IOException {
		if (!directory.exists()) {
			directory.mkdir();
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory.toString()
					+ " is not a directory!");
		}
		String name = saveFileName(profile, map);
		File weightsFile = new File(directory, name + ".weights");
		File arcFlagsFile = new File(directory, name + ".arcflags");
		File timeFile = new File(directory, name + ".time");
		getWeights().save(weightsFile);
		getArcFlags().save(arcFlagsFile);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(timeFile))) {
			bw.write(Integer.toString(calculationTime) + "\n");
		}
	}

	/**
	 * Loads a {@link ProfileMapCombination} from the given directory by loading
	 * the {@link #getWeights() Weights}, the {@link #getArcFlags() ArcFlags}
	 * and the {@link #getCalculationTime() calculation time} from the files
	 * {@code &lt;name&gt;.weights}, {@code &lt;name&gt;.arcflags} and
	 * {@code &lt;name&gt;.time} (where {@code &lt;name&gt;} is the name of the
	 * map " + " the name of the profile).
	 * 
	 * @param profile
	 *            The profile.
	 * @param map
	 *            The map.
	 * @param directory
	 *            The directory from which the {@link ProfileMapCombination}
	 *            should be loaded.
	 * @return A {@link ProfileMapCombination} containing the {@link Weights}
	 *         and {@link ArcFlags} from the given directory.
	 * @throws IOException
	 *             If the files can’t be read.
	 */
	public static ProfileMapCombination load(Profile profile, StreetMap map,
			File directory) throws IOException {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory.toString()
					+ " is not a directory!");
		}
		String name = saveFileName(profile, map);
		File weightsFile = new File(directory, name + ".weights");
		File arcFlagsFile = new File(directory, name + ".arcflags");
		File timeFile = new File(directory, name + ".time");
		Weights weights = Weights.load(weightsFile);
		ArcFlags arcFlags = ArcFlags.load(arcFlagsFile);
		final int time;
		try (BufferedReader br = new BufferedReader(new FileReader(timeFile))) {
			time = Integer.parseInt(br.readLine());
		}
		return new ProfileMapCombination(map, profile, weights, arcFlags, time);
	}

	/**
	 * Loads a precalculation from the given directory in the same way as
	 * {@link #load(File)}, except that the {@link Weights} and {@link Arcflags}
	 * are loaded lazily – only on the first access.
	 * 
	 * @param directory
	 *            The directory from which the {@link ProfileMapCombination}
	 *            should be loaded.
	 * @return A kind of {@link ProfileMapCombination} that loads the
	 *         {@link Weights} and the {@link ArcFlags} lazily.
	 * @throws IOException
	 *             If the calculation time file (which is read eagerly) can’t be
	 *             read.
	 */
	public static ProfileMapCombination loadLazily(Profile profile,
			StreetMap map, File directory) throws IOException {
		String name = saveFileName(profile, map);
		File weightsFile = new File(directory, name + ".weights");
		File arcFlagsFile = new File(directory, name + ".arcflags");
		File timeFile = new File(directory, name + ".time");
		final int time;
		try (BufferedReader br = new BufferedReader(new FileReader(timeFile))) {
			time = Integer.parseInt(br.readLine());
		}
		return new LazyProfileMapCombination(profile, map, weightsFile,
				arcFlagsFile, time);
	}

	private static class LazyProfileMapCombination extends
			ProfileMapCombination {
		private final File weightsFile;
		private final File arcFlagsFile;

		public LazyProfileMapCombination(Profile profile, StreetMap map,
				File weightsFile, File arcFlagsFile, int calculationTime) {
			super(map, profile);
			this.weightsFile = weightsFile;
			this.arcFlagsFile = arcFlagsFile;
			this.calculationTime = calculationTime;
		}

		@Override
		public boolean isCalculated() {
			// un-lazy
			getWeights();
			getArcFlags();
			return super.isCalculated();
		}

		@Override
		public Weights getWeights() {
			if (this.weights == null) {
				try {
					this.weights = Weights.load(weightsFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return weights;
		}

		@Override
		public ArcFlags getArcFlags() {
			if (this.arcFlags == null) {
				try {
					this.arcFlags = ArcFlags.load(arcFlagsFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return arcFlags;
		}

		@Override
		public void ensureLoaded(ProgressReporter reporter) {
			reporter.setSubTasks(new float[] { .9f, .05f, .05f });
			reporter.pushTask("Lade Karte");
			super.ensureLoaded(reporter);
			reporter.nextTask("Lade Kantengewichte");
			getWeights();
			reporter.nextTask("Lade Arc-Flags");
			getArcFlags();
			reporter.popTask();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProfileMapCombination) {
			ProfileMapCombination other = (ProfileMapCombination) obj;
			return profile.equals(other.profile) && map.equals(other.map)
					&& getArcFlags().equals(other.getArcFlags())
					&& getWeights().equals(other.getWeights());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return calculationTime ^ profile.hashCode() ^ map.hashCode();
	}
}
