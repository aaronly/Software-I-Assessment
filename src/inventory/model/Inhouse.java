package inventory.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * An individual part that is made in house.
 * Includes the ID number for the machine the part is made on.
 * @author Aaron Echols
 *
 */
public class Inhouse extends Part{
	
	private IntegerProperty machineID = new SimpleIntegerProperty();
	
	/**
	 * Create a new part that is made in house with default values.
	 */
	public Inhouse() {
		this("New Part", 0, 0);
		setName(getName() + " " + getPartID());
	}
	/**
	 * Create a new part that is made in house. Minimum, maximum, and
	 * current inventory levels are set to zero.
	 * @param name the name of the part
	 * @param price the cost to make the part
	 * @param machineID the ID number of the machine on which this part is made
	 * @throws IllegalArgumentException if a value is not acceptable
	 */
	public Inhouse(String name, double price, int machineID) throws IllegalArgumentException{
		this(name, price, 0, 0, 0, machineID);
	}
	/**
	 * Create a new part that is made in house.
	 * @param name the name of the part
	 * @param price the cost to make the part
	 * @param instock the amount of stock of this part in the inventory
	 * @param min the minimum amount of stock to keep in the inventory
	 * @param max the maximum amount of stock to keep in the inventory
	 * @param machineID the ID number of the machine on which this part is made
	 * @throws IllegalArgumentException if a supplied value is not acceptable
	 */
	public Inhouse(String name, double price, int instock, int min, int max, int machineID) throws IllegalArgumentException{

		setPartID();

		setName(name);
		setPrice(price);
		setMax(max);
		setMin(min);
		setInstock(instock);
		
		setMachineID(machineID);
	}
	
	/**
	 * 
	 * @return the ID number of the machine on which this part is made
	 */
	public int getMachineID() {
		return machineID.get();
	}
	/**
	 * 
	 * @param machineID the ID number of the machine on which this part is made
	 */
	public void setMachineID(int machineID) {
		this.machineID.set(machineID);
	}
	
	public IntegerProperty machineIDProperty() {
		return machineID;
	}
}