package edu.kit.pse.ws2013.routekit.profiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * A vehicle profile.
 */
public class Profile implements Cloneable {

	/**
	 * The name of the profile.
	 */
	private String name;
	/**
	 * The type of the vehicle.
	 */
	private VehicleType vehicleType;
	/**
	 * The height of the vehicle, in centimeters.
	 */
	private int height;
	/**
	 * The width of the vehicle, in centimeters.
	 */
	private int width;
	/**
	 * The weight of the vehicle, in kilograms.
	 */
	private int weight;
	/**
	 * The average speed of the vehicle on a highway, in kilometers per hour.
	 */
	private int speedHighway;
	/**
	 * The average speed of the vehicle on an ordinary (country) road, in
	 * kilometers per hour.
	 */
	private int speedRoad;
	/**
	 * If the profile is a default profile or not.
	 */
	private boolean isDefault;

	public static final Profile defaultCar = new Profile("PKW (Standard)",
			VehicleType.Car, 160, 160, 1400, 120, 80, true);
	public static final Profile defaultTruck = new Profile("LKW (Standard)",
			VehicleType.Truck, 350, 240, 20000, 80, 80, true);

	/**
	 * Creates a new non-default {@link Profile} with the specified parameters.
	 * 
	 * @param name
	 *            The name of the profile.
	 * @param vehicleType
	 *            The type of the vehicle.
	 * @param height
	 *            The height of the vehicle, in centimeters.
	 * @param width
	 *            The width of the vehicle, in centimeters.
	 * @param weight
	 *            The weight of the vehicle, in kilograms.
	 * @param speedHighway
	 *            The average speed of the vehicle on a highway, in kilometers
	 *            per hour.
	 * @param speedRoad
	 *            The average speed of the vehicle on an ordinary (country)
	 *            road, in kilometers per hour.
	 */
	public Profile(String name, VehicleType vehicleType, int height, int width,
			int weight, int speedHighway, int speedRoad) {
		this(name, vehicleType, height, width, weight, speedHighway, speedRoad,
				false);
	}

	/**
	 * Creates a new {@link Profile} with the specified parameters.
	 * 
	 * @param name
	 *            The name of the profile.
	 * @param vehicleType
	 *            The type of the vehicle.
	 * @param height
	 *            The height of the vehicle, in centimeters.
	 * @param width
	 *            The width of the vehicle, in centimeters.
	 * @param weight
	 *            The weight of the vehicle, in kilograms.
	 * @param speedHighway
	 *            The average speed of the vehicle on a highway, in kilometers
	 *            per hour.
	 * @param speedRoad
	 *            The average speed of the vehicle on an ordinary (country)
	 *            road, in kilometers per hour.
	 * @param isDefault
	 *            If the profile is a default profile or not.
	 */
	private Profile(String name, VehicleType vehicleType, int height,
			int width, int weight, int speedHighway, int speedRoad,
			boolean isDefault) {
		this.name = name;
		this.vehicleType = vehicleType;
		this.height = height;
		this.width = width;
		this.weight = weight;
		this.speedHighway = speedHighway;
		this.speedRoad = speedRoad;
		this.isDefault = isDefault;
	}

	/**
	 * Creates and returns a non-default copy of this profile.
	 */
	@Override
	public Profile clone() {
		return new Profile(name, vehicleType, height, width, weight,
				speedHighway, speedRoad);
	}

	/**
	 * Saves the profile to the specified file using the Java {@link Properties}
	 * format.
	 * 
	 * @param file
	 *            The file to which the profile should be saved.
	 * @throws IOException
	 *             If the file can’t be written.
	 * @see #load(File)
	 */
	public void save(File file) throws IOException {
		Properties p = new Properties();
		p.setProperty("name", name);
		p.setProperty("vehicleType", vehicleType.name());
		p.setProperty("height", Integer.toString(height));
		p.setProperty("width", Integer.toString(width));
		p.setProperty("weight", Integer.toString(weight));
		p.setProperty("speedHighway", Integer.toString(speedHighway));
		p.setProperty("speedRoad", Integer.toString(speedRoad));
		try (FileWriter writer = new FileWriter(file)) {
			p.store(new FileWriter(file), null);
		}
	}

	/**
	 * Loads a profile from the specified file and returns it.
	 * 
	 * @param file
	 *            The file from which the profile should be loaded.
	 * @return A new profile with the values from the file.
	 * @throws IOException
	 *             If the file can’t be read or parsed.
	 * @throws FileNotFoundException
	 *             If the file doesn’t exist.
	 * @see #save(File)
	 */
	public static Profile load(File file) throws FileNotFoundException,
			IOException {
		Properties p = new Properties();
		try (FileReader reader = new FileReader(file)) {
			p.load(reader);
		}
		String name = p.getProperty("name");
		VehicleType vehicleType = VehicleType.valueOf(p
				.getProperty("vehicleType"));
		Integer height = parseIntElseNull(p.getProperty("height"));
		Integer width = parseIntElseNull(p.getProperty("width"));
		Integer weight = parseIntElseNull(p.getProperty("weight"));
		Integer speedHighway = parseIntElseNull(p.getProperty("speedHighway"));
		Integer speedRoad = parseIntElseNull(p.getProperty("speedRoad"));

		if (name == null) {
			throw new IOException(
					"name missing or cannot be parsed in profile file '" + file
							+ "'");
		}
		if (vehicleType == null) {
			throw new IOException(
					"vehicleType missing or cannot be parsed in profile file '"
							+ file + "'");
		}
		if (width == null) {
			throw new IOException(
					"width missing or cannot be parsed in profile file '"
							+ file + "'");
		}
		if (height == null) {
			throw new IOException(
					"height missing or cannot be parsed in profile file '"
							+ file + "'");
		}
		if (weight == null) {
			throw new IOException(
					"weight missing or cannot be parsed in profile file '"
							+ file + "'");
		}
		if (speedHighway == null) {
			throw new IOException(
					"speedHighway missing or cannot be parsed in profile file '"
							+ file + "'");
		}
		if (speedRoad == null) {
			throw new IOException(
					"speedRoad missing or cannot be parsed in profile file '"
							+ file + "'");
		}

		return new Profile(name, vehicleType, height, width, weight,
				speedHighway, speedRoad);
	}

	private static Integer parseIntElseNull(String s) {
		try {
			return new Integer(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * <p>
	 * Indicates if the profile is a default profile.
	 * </p>
	 * <p>
	 * Default profiles are different than normal profiles in that it’s not
	 * allowed to set their values.
	 * </p>
	 * 
	 * @return {@code true} if the profile is a default profile, else
	 *         {@code false}.
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * Gets the name of the profile.
	 * 
	 * @return The name of the profile.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>
	 * Sets the name of the profile.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The name of the profile.
	 */
	public void setName(String name) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.name = name;
	}

	/**
	 * Gets the type of the vehicle.
	 * 
	 * @return The type of the vehicle.
	 */
	public VehicleType getVehicleType() {
		return vehicleType;
	}

	/**
	 * <p>
	 * Sets the type of the vehicle.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The type of the vehicle.
	 */
	public void setVehicleType(VehicleType vehicleType) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.vehicleType = vehicleType;
	}

	/**
	 * Gets the height of the vehicle, in centimeters.
	 * 
	 * @return The height of the vehicle, in centimeters.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * <p>
	 * Sets the height of the vehicle, in centimeters.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The height of the vehicle, in centimeters.
	 */
	public void setHeight(int height) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.height = height;
	}

	/**
	 * Gets the width of the vehicle, in centimeters.
	 * 
	 * @return The width of the vehicle, in centimeters.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * <p>
	 * Sets the width of the vehicle, in centimeters.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The width of the vehicle, in centimeters.
	 */
	public void setWidth(int width) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.width = width;
	}

	/**
	 * Gets the weight of the vehicle, in kilograms.
	 * 
	 * @return The weight of the vehicle, in kilograms.
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * <p>
	 * Sets the weight of the vehicle, in kilograms.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The weight of the vehicle, in kilograms.
	 */
	public void setWeight(int weight) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.weight = weight;
	}

	/**
	 * Gets the average speed of the vehicle on a highway, in kilometers per
	 * hour.
	 * 
	 * @return The average speed of the vehicle on a highway, in kilometers per
	 *         hour.
	 */
	public int getSpeedHighway() {
		return speedHighway;
	}

	/**
	 * <p>
	 * Sets the average speed of the vehicle on a highway, in kilometers per
	 * hour.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The average speed of the vehicle on a highway, in kilometers
	 *            per hour.
	 */
	public void setSpeedHighway(int speedHighway) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.speedHighway = speedHighway;
	}

	/**
	 * Gets the average speed of the vehicle on an ordinary (country) road, in
	 * kilometers per hour.
	 * 
	 * @return The average speed of the vehicle on an ordinary (country) road,
	 *         in kilometers per hour.
	 */
	public int getSpeedRoad() {
		return speedRoad;
	}

	/**
	 * <p>
	 * Sets the average speed of the vehicle on an ordinary (country) road, in
	 * kilometers per hour.
	 * </p>
	 * <p>
	 * Note that this is not allowed for {@link #isDefault() default} profiles.
	 * </p>
	 * 
	 * @param name
	 *            The average speed of the vehicle on an ordinary (country)
	 *            road, in kilometers per hour.
	 */
	public void setSpeedRoad(int speedRoad) {
		if (isDefault()) {
			throw new IllegalStateException(
					"Can't set parameter of a default profile!");
		}
		this.speedRoad = speedRoad;
	}

	/**
	 * Indicates whether this profile is equal to the given object. If
	 * {@code obj} is a {@code Profile}, then this is equivalent to
	 * <code>equals(obj, true)</code>, i.&nbsp;e. the profiles must be either
	 * both {@link #isDefault() default} or both non-default.
	 * 
	 * @return {@code true} if the two profiles are equal, {@code false}
	 *         otherwise.
	 * @see #equals(Profile, boolean)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Profile) {
			return equals((Profile) obj, true);
		}
		return false;
	}

	/**
	 * Indicates whether this profile is equal to the given profile.
	 * 
	 * @param other
	 *            The other profile.
	 * @param checkDefault
	 *            Whether to include the {@link #isDefault() default} property
	 *            in the check or not. For example,
	 *            <code>defaultCar.equals(defaultCar.clone(), true)</code> is
	 *            {@code false} because the {@link #clone() clone} isn’t a
	 *            default profile, but
	 *            <code>defaultCar.equals(defaultCar.clone(), false)</code> is
	 *            {@code true}.
	 * @return {@code true} if the two profiles are equal, {@code false}
	 *         otherwise.
	 */
	public boolean equals(Profile other, boolean checkDefault) {
		if (!this.name.equals(other.name)) {
			return false;
		}
		if (!this.vehicleType.equals(other.vehicleType)) {
			return false;
		}
		if (this.height != other.height) {
			return false;
		}
		if (this.width != other.width) {
			return false;
		}
		if (this.weight != other.weight) {
			return false;
		}
		if (this.speedHighway != other.speedHighway) {
			return false;
		}
		if (this.speedRoad != other.speedRoad) {
			return false;
		}
		if (checkDefault && this.isDefault != other.isDefault) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((vehicleType == null) ? 0 : vehicleType.hashCode());
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + weight;
		result = prime * result + speedHighway;
		result = prime * result + speedRoad;
		result = prime * result + (isDefault ? 1231 : 1237);
		return result;
	}
}
