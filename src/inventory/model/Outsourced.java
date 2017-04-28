package inventory.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * An individual part that is made purchased from a supplier.
 * Includes the company name which supplies the part.
 * @author Aaron Echols
 *
 */
public class Outsourced extends Part{
	
	private StringProperty companyName = new SimpleStringProperty();
	
	/**
	 * Create a new part that is supplied by another company with default values.
	 */
	public Outsourced() {
		this("New Part", 0, "No Supplier Specified");
		setName(getName() + " " + getPartID());
	}
	/**
	 * Create a new part that is supplied by another company. Minimum, maximum, and
	 * current inventory levels are set to zero.
	 * @param name the name of the part
	 * @param price the price of the part
	 * @param companyName the name of the company the supplies the part
	 * @throws IllegalArgumentException if a supplied value is not acceptable
	 */
	public Outsourced(String name, double price, String companyName) throws IllegalArgumentException {
		this(name, price, 0, 0, 0, companyName);
	}
	/**
	 * Create a new part that is supplied by another company.
	 * @param name the name of the part
	 * @param price the price of the part
	 * @param instock the amount of stock of this part in the inventory
	 * @param min the minimum amount of stock to keep in the inventory
	 * @param max the maximum amount of stock to keep in the inventory
	 * @param companyName the name of the company the supplies the part
	 * @throws IllegalArgumentException if a supplied value is not acceptable
	 */
	public Outsourced(String name, double price, int instock, int min, int max, String companyName) throws IllegalArgumentException {

		setPartID();
		
		setName(name);
		setPrice(price);
		setMax(max);
		setMin(min);
		setInstock(instock);
		
		setCompanyName(companyName);
	}
	
	/**
	 * 
	 * @return the name of the company the supplies the part
	 */
	public String getCompanyName() {
		return companyName.get();
	}
	/**
	 * 
	 * @param companyName the name of the company the supplies the part
	 */
	public void setCompanyName(String companyName) {
		if(companyName == null || companyName.isEmpty()) {
			companyName = "No Supplier Specified";
		}
		this.companyName.set(companyName);
	}
	
	public StringProperty companyNameProperty() {
		return companyName;
	}
}