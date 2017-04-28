package inventory.model;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A finished product that contains one or more parts.
 * @author Aaron Echols
 *
 */
public class Product {
	
	private StringProperty name = new SimpleStringProperty();
	private IntegerProperty productID = new SimpleIntegerProperty();
	private DoubleProperty price = new SimpleDoubleProperty();
	private IntegerProperty instock = new SimpleIntegerProperty();
	private IntegerProperty min = new SimpleIntegerProperty();
	private IntegerProperty max = new SimpleIntegerProperty();
	private ObservableList<Part> parts = FXCollections.observableArrayList();
	
	private static int nextProductID = 1;

	public Product(String name, double price, List<Part> parts) throws IllegalArgumentException{
		this(name, price, 0, 0, 0, parts);
	}
	public Product(String name, double price, Part firstPart)  throws IllegalArgumentException{
		this(name, price, 0, 0, 0, firstPart);
	}
	public Product(String name, double price, int instock, int min, int max, Part firstPart)  throws IllegalArgumentException{
		this(name, price, instock, min, max, FXCollections.observableArrayList(firstPart));
	}
	public Product(String name, double price, int instock, int min, int max, List<Part> parts) throws IllegalArgumentException{
		setProductID();
		
		this.parts.setAll(parts);
		
		setName(name);
		setPrice(price);
		setMax(max);
		setMin(min);
		setInstock(instock);
	}
	
	/**
	 * 
	 * @return the name of the product
	 */
	public String getName() {
		return name.get();
	}
	/**
     * 
     * @return the iD number of the product
     */
	public int getProductID() {
		return productID.get();
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
	public IntegerProperty productIDProperty() {
		return productID;
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
	 * @param name the name of the product
	 */
	public void setName(String name) {
		if(name == null || name.isEmpty()) {
			name = "New Product";
		}
		this.name.set(name);
	}
	/**
	 * This will set the product ID to the next available number.
	 * NOTE: ProductID is not able to be set manually. This ensures that
	 * every ProductID is unique.
	 */
	public void setProductID(){
		// assign the next available number and increment counter
		this.productID.set(nextProductID++); 
	}
	/**
	 * 
	 * @param price the price in US dollars
	 * @throws IllegalArgumentException if amount is negative
	 */
	public void setPrice(double price) throws IllegalArgumentException{
		if(price < 0) {
			throw new IllegalArgumentException("Price cannot be negative.");
		}
		
		double total = 0;
		// get the total sum of the parts
		for(Part part : parts) {
			total += part.getPrice();
		}
		
		if(price < total) {
			throw new IllegalArgumentException("Price cannot be less than the sum of its parts");
		}
		
		this.price.set(price);
	}
	/**
	 * 
	 * @param instock the amount of stock in the inventory
	 * @throws IllegalArgumentException if amount is greater than the maximum or less than the minimum
	 */
	public void setInstock(int instock) throws IllegalArgumentException{
		if(instock < getMin()) {
			throw new IllegalArgumentException("Amount of stock needs to be less than or equal to the minumum.");
		} else if(instock > getMax()){
			throw new IllegalArgumentException("Amount of stock needs to be greater than or equal to the maximum.");
		}
		
		this.instock.set(instock);
	}
	/**
	 * 
	 * @param min the minimum amount of stock to keep in the inventory
	 * @throws IllegalArgumentException if minimum is greater than the current maximum
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
	 * @throws IllegalArgumentException if maximum is less than the current minimum
	 */
	public void setMax(int max) throws IllegalArgumentException{
		if(max < getMin()) {
			throw new IllegalArgumentException("Maximum amount of stock needs to be greater than or equal to the the minimum.");
		}
		this.max.set(max);
	}

    /**
	 * add an individual part to the product
	 * @param partToAdd the part to add to the product
	 */
	public void addPart(Part partToAdd) {
		this.parts.add(partToAdd);
	}
	/**
	 * find a part by ID number in the product
	 * @param partID the ID number of the part
	 * @return the part if it is in the product, null if not
	 */
	public Part lookupPart(int partID) {
		Part partFound = null;
		
		for(Part part : parts) {
			if(part.getPartID() == partID) {
				partFound = part;
				break;
			}
		}
		
		return partFound;
	}
	/**
	 * remove a part from the product
	 * @param partID the ID number of the part to remove
	 * @return true if the part was removed successfully, false if not
	 */
	public boolean removePart(int partID) {
		boolean result = false;
	
		if (parts == null || parts.isEmpty()) {
			return result;
		}
		
		Part partToRemove = lookupPart(partID);

		if(partToRemove != null) {
			result = this.parts.remove(partToRemove);
		}

		return result;
	}
	/**
	 * 
	 * @return the list of parts contained in this product
	 */
	public ObservableList<Part> getParts() {
		return parts;
	}
	
	/**
	 * Copy the product ID from one product to this product.
	 * The allows a new product to replace an existing product in the inventory
	 * and keep the same ID number.
	 * @param oldProduct the product to get the product ID number from
	 */
	public void copyProductID(Product oldProduct) {
		this.productID.set(oldProduct.getProductID());
	}

	/*
	 * This method was not needed. Any part that needs to be updated in
	 * a product is handled by another method. Part updates are handled
	 * implicitly by the updateProduct method in the Inventory Class.
	 * 
	 * public void updatePart(int partID) {}
	 * */
}
