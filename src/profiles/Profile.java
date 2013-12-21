package profiles;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
/**
 * Ein Fahrzeugprofil.
 */
public class Profile implements Cloneable {
	
	private String name;
	private VehicleType vehicleType;
	private int height;
	private int width;
	private int weight;
	private int speedHighway;
	private int speedRoad;
	private boolean isDefault;
	
	public static Profile defaultCar =
			new Profile("PKW (Standard)", VehicleType.Car, 160, 160, 1400, 120, 80, true);
	public static Profile defaultTruck =
			new Profile("LKW (Standard)", VehicleType.Truck, 350, 240, 20000, 80, 80, true);
	
	public Profile(String name, VehicleType vehicleType, int height, int width, int weight, int speedHighway, int speedRoad) {
		this(name, vehicleType, height, width, weight, speedHighway, speedRoad, false);
	}
	
	private Profile(String name, VehicleType vehicleType, int height, int width, int weight, int speedHighway, int speedRoad, boolean isDefault) {
		this.name = name;
		this.vehicleType = vehicleType;
		this.height = height;
		this.width = width;
		this.weight = weight;
		this.speedHighway = speedHighway;
		this.speedRoad = speedRoad;
		this.isDefault = isDefault;
	}
	
	@Override
	public Profile clone() {
		return new Profile(name, vehicleType, height, width, weight, speedHighway, speedRoad);
	}
	
	/**
	 * Speichert das Profil in die angegebene Datei.
	 * 
	 * @param file
	 *            Die Datei, in die das Profil gespeichert wird.
	 * @throws IOException 
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
	 * (statisch) Lädt ein Profil aus der angegebenen Datei und gibt es zurück.
	 * 
	 * @param file
	 *            Die Datei, aus der das Profil geladen wird.
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Profile load(File file) throws FileNotFoundException, IOException {
		Properties p = new Properties();
		try (FileReader reader = new FileReader(file)) {
			p.load(reader);
		}
		String name = p.getProperty("name");
		VehicleType vehicleType = VehicleType.valueOf(p.getProperty("vehicleType"));
		Integer height = parseIntElseNull(p.getProperty("height"));
		Integer width = parseIntElseNull(p.getProperty("width"));
		Integer weight = parseIntElseNull(p.getProperty("weight"));
		Integer speedHighway = parseIntElseNull(p.getProperty("speedHighway"));
		Integer speedRoad = parseIntElseNull(p.getProperty("speedRoad"));

		if (name == null) {
			throw new IOException("name missing or cannot be parsed in profile file '" + file + "'");
		}
		if (vehicleType == null) {
			throw new IOException("vehicleType missing or cannot be parsed in profile file '" + file + "'");
		}
		if (width == null) {
			throw new IOException("width missing or cannot be parsed in profile file '" + file + "'");
		}
		if (height == null) {
			throw new IOException("height missing or cannot be parsed in profile file '" + file + "'");
		}
		if (weight == null) {
			throw new IOException("weight missing or cannot be parsed in profile file '" + file + "'");
		}
		if (speedHighway == null) {
			throw new IOException("speedHighway missing or cannot be parsed in profile file '" + file + "'");
		}
		if (speedRoad == null) {
			throw new IOException("speedRoad missing or cannot be parsed in profile file '" + file + "'");
		}
		
		return new Profile(name, vehicleType, height, width, weight, speedHighway, speedRoad);
	}
	private static Integer parseIntElseNull(String s) {
		try {
			return new Integer(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * Gibt an, ob es sich um ein Standardprofil handelt oder nicht.
	 * 
	 * @return
	 */
	public boolean isDefault() {
		return isDefault;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.name = name;
	}
	
	public VehicleType getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(VehicleType vehicleType) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.vehicleType = vehicleType;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.width = width;
	}
	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.weight = weight;
	}
	
	public int getSpeedHighway() {
		return speedHighway;
	}
	public void setSpeedHighway(int speedHighway) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.speedHighway = speedHighway;
	}
	
	public int getSpeedRoad() {
		return speedRoad;
	}
	public void setSpeedRoad(int speedRoad) {
		if (isDefault())
			throw new IllegalStateException("Can't set parameter of a default profile!");
		this.speedRoad = speedRoad;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Profile) {
			return equals((Profile)obj, true);
		}
		return false;
	}
	
	public boolean equals(Profile other, boolean checkDefault) {
		if(!this.name.equals(other.name))
			return false;
		if(!this.vehicleType.equals(other.vehicleType))
			return false;
		if(this.height != other.height)
			return false;
		if(this.width != other.width)
			return false;
		if(this.weight != other.weight)
			return false;
		if(this.speedHighway != other.speedHighway)
			return false;
		if(this.speedRoad != other.speedRoad)
			return false;
		if(checkDefault && this.isDefault != other.isDefault)
			return false;
		return true;
	}
}
