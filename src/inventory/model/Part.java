package inventory.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * An individual part stored in the inventory and/or included in a final product.
 * The is an abstract superclass meant to be derived into more specific types of parts.
 * Includes basic part info such as: name, price, ID number, and stock information.
 * @author Aaron Echols
 *
 */
public abstract class Part {
	
	private StringProperty name = new SimpleStringProperty();
	private IntegerProperty partID = new SimpleIntegerProperty();
	private DoubleProperty price = new SimpleDoubleProperty();
	private IntegerProperty instock = new SimpleIntegerProperty();
	private IntegerProperty min = new SimpleIntegerProperty();
	private IntegerProperty max = new SimpleIntegerProperty();
	
	/**
	 * A static counter that keeps up with the next part ID available for use.
	 */
	private static int nextPartID = 1;
	
	/**
	 * 
	 * @return the name of the part
	 */
    public String getName() {
		return name.get();
	}
    /**
     * 
     * @return the iD number of the part
     */
    public int getPartID() {
		return partID.get();
	}
	/**
	 * 
	 * @return the price in US dollars
	 */
	public double getPrice() {
		return price.get();
	}
	/**
	 * 
	 * @return the amount of stock in the inventory
	 */
	public int getInstock() {
		return instock.get();
	}
    /**
	 * 
	 * @return the minimum amount of stock to keep in the inventory
	 */
	public int getMin() {
		return min.get();
	}
	/**
	 * 
	 * @return the maximum amount of stock to keep in the inventory
	 */
	public int getMax() {
		return max.get();
	}

	public StringProperty nameProperty() {
        return name;
    }
	public IntegerProperty partIDProperty() {
		return partID;
	}
	public DoubleProperty priceProperty() {
		return price;
	}
	public IntegerProperty instockProperty() {
		return instock;
	}
	public IntegerProperty minProperty() {
		return min;
	}
	public IntegerProperty maxProperty() {
		return max;
	}

	/**
	 * 
	 * @param name the name of the part
	 */
	public void setName(String name) {
		if(name == null || name.isEmpty()) {
			name = "New Part";
		}
		this.name.set(name);
	}
	/**
	 * This will set the part ID to the next available number.
	 * NOTE: PartID is not able to be set manually. This ensures that
	 * every PartID is unique. That is why this method has no input parameter.
	 */
	public void setPartID(){
		// assign the next available number and increment counter
		this.partID.set(Part.nextPartID++);
	}
	/**
	 * 
	 * @param price the price in US dollars
	 * @throws IllegalArgumentException if amount is negative
	 */
	public void setPrice(double price) throws IllegalArgumentException{
		if(price < 0) {
			throw new IllegalArgumentException("Price cannot be negative");
		}
		
		this.price.set(price);
	}
	/**
	 * 
	 * @param instock the amount of stock in the inventory
	 * @throws IllegalArgumentException if amount is above the maximum or below the minimum
	 */
	public void setInstock(int instock) throws IllegalArgumentException{
		if(instock < getMin()) {
			throw new IllegalArgumentException("Amount of stock needs to be greater than or equal to the minumum.");
		} else if(instock > getMax()){
			throw new IllegalArgumentException("Amount of stock needs to be less than or equal to the maximum.");
		}
		
		this.instock.set(instock);
	}
	/**
	 * 
	 * @param min the minimum amount of stock to keep in the inventory
	 * @throws IllegalArgumentException if minimum is above the current maximum
	 */
	public void setMin(int min) throws IllegalArgumentException{
		if(min > getMax()) {
			throw new IllegalArgumentException("Minimum amount of stock needs to be less than or equal to the the maximum.");
		}
		this.min.set(min);
	}
	/**
	 * 
	 * @param max the maximum amount of stock to keep in the inventory
	 */
	public void setMax(int max) throws IllegalArgumentException{
		if(max < getMin()) {
			throw new IllegalArgumentException("Maximum amount of stock needs to be greater than or equal to the the minimum.");
		}
		this.max.set(max);
	}

	/**
	 * Copy the part ID from one part to this part.
	 * The allows a new part to replace an existing part in the inventory
	 * and keep the same ID number.
	 * @param oldPart the part to get the part ID number from
	 */
	public void copyPartID(Part oldPart) {
		this.partID.set(oldPart.getPartID());
	}
}
