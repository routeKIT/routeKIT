package profiles;
import java.io.File;
/**
 * Ein Fahrzeugprofil.
 */
public class Profile {
	
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
	
	/**
	 * Speichert das Profil in die angegebene Datei.
	 * 
	 * @param file
	 *            Die Datei, in die das Profil gespeichert wird.
	 */
	public void save(File file) {
	}
	/**
	 * (statisch) Lädt ein Profil aus der angegebenen Datei und gibt es zurück.
	 * 
	 * @param file
	 *            Die Datei, aus der das Profil geladen wird.
	 * @return
	 */
	public static Profile load(File file) {
		return null;
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
}
