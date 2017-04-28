package inventory.model;

import inventory.util.PriceFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The main inventory class that holds all of the parts and products
 * @author Aaron Echols
 *
 */
public class Inventory {

	private ObservableList<Part> partList;
    private ObservableList<Product> productList;
    
	/**
	 * Create a new empty inventory and intialize the part and product lists.
	 */
	public Inventory() {
		partList = FXCollections.observableArrayList();
		productList = FXCollections.observableArrayList();
	}
	
	/**
	 * 
	 * @return a list of all parts currently in the inventory
	 */
    public ObservableList<Part> getPartList() {
    	return partList;
    }
    /**
     * 
     * @return a list of all products currently in the inventory
     */
    public ObservableList<Product> getProductList() {
    	return productList;
    }

    // ------------------------ PART METHODS ------------------------ 
    
    /**
     * Add a part to the inventory.
     * @param partToAdd the part to be added
     */
	public void addPart(Part partToAdd) {
		// Make sure the part to add is not null
		if(partToAdd != null) {
			partList.add(partToAdd);
		}
	}
	/**
	 * Remove a part from the inventory.
	 * @param partToRemove the part to remove
	 * @return true if the part was removed successfully, false if not
	 */
	public boolean removePart(Part partToRemove) {
		boolean result = false;

		if(partToRemove != null) {
			result = partList.remove(partToRemove);
		}

		return result;
	}
	/**
	 * Find a part in the inventory by name.
	 * @param name the name of the part to find
	 * @return the first part in the inventory that matches the specified name,
	 * null if no matching part was found
	 */
	public Part lookupPart(String name) {
		Part partFound = null;
		
		// loop through all parts and search for a complete match of the name (case insensitive)
		for(Part part : partList) {
			if(name.toLowerCase().equals(part.getName().toLowerCase())) {
				partFound = part;
				break;
			}
		}
		
		return partFound;
	}
	/**
	 * Update the part with new information.
	 * @param oldPart the part being updated
	 * @param newPart a part with updated values
	 */
	public void updatePart(Part oldPart, Part newPart) {
		newPart.copyPartID(oldPart);
		removePart(oldPart);
		addPart(newPart);
	}
	/**
	 * Searches all the parts for a given string. Search is case insensitive.
	 * Searches the name, price, company name, and machine ID for possible matches.
	 * @param str the string to search for
	 * @return an observable list of parts
	 */
	public ObservableList<Part> searchForPartByString(String str) {
		ObservableList<Part> partsFound = FXCollections.observableArrayList();
		
		String name = null;
		String companyName = null;
		String machineID = null;
		String price = null;
		
		// loop through all the parts
		for(Part part : partList) {
			// get part properties to search on
			name = part.getName();
			machineID = part instanceof Inhouse ? String.valueOf(((Inhouse)part).getMachineID()) : "";
			companyName = part instanceof Outsourced ? ((Outsourced)part).getCompanyName() : "";
			price = PriceFormatter.format(part.getPrice());
			
			// if there is a partial match in any of the search properties,
			// add the part to the list
			if(name.toLowerCase().contains(str.toLowerCase()) ||
				machineID.toLowerCase().contains(str.toLowerCase()) ||
				companyName.toLowerCase().contains(str.toLowerCase()) ||
				price.toLowerCase().contains(str.toLowerCase())) {
				partsFound.add(part);
			}
		}
		
		return partsFound;
	}
	
	// ------------------------ PRODUCT METHODS ------------------------ 
	
    /**
     * Add a product to the inventory.
     * @param partToAdd the product to be added
     */
	public void addProduct(Product productToAdd) {
		if (productToAdd != null) {
			productList.add(productToAdd);
		}
	}
	/**
	 * Remove a product from the inventory. The method will not allow a product
	 * that still contains parts to be deleted.
	 * @param productToRemove the product to remove
	 * @return true if the product was removed successfully, false if not
	 * @throws Exception if there are still parts contained in the product
	 */
	public boolean removeProduct(Product productToRemove) throws Exception {
		boolean result = false;
		if (productToRemove ==  null) return false;
		
		// if the part list is not empty
		if(!productToRemove.getParts().isEmpty()) {
			throw new Exception("Product still contains one or more parts.");
		}
		
		result = this.productList.remove(productToRemove);
		
		return result;
	}
	/**
	 * Remove a product from the inventory even if it still contains parts.
	 * @param productToRemove the product to remove
	 * @return true if the product was removed successfully, false if not
	 */
	public boolean removeNotEmptyProduct(Product productToRemove) {
		boolean result = false;
		
		if (productToRemove ==  null) return result;
		
		result = this.productList.remove(productToRemove);
		
		return result;
	}
	/**
	 * Find a product in the inventory by name.
	 * @param name the name of the product to find  (case insensitive)
	 * @return the first product in the inventory that matches the specified name,
	 * null if no matching product was found
	 */
	public Product lookupProduct(String name) {
		Product productFound = null;
		
		// loop through all products and search for a complete match of the name
		for(Product product : productList) {
			if(name.toLowerCase().equals(product.getName().toLowerCase())) {
				productFound = product;
				break;
			}
		}
		
		return productFound;
	}
	/**
	 * Update the product with new information.
	 * @param oldProduct the product being updated
	 * @param newProduct a product with updated values
	 */
	public void updateProduct(Product oldProduct, Product newProduct) {
		newProduct.copyProductID(oldProduct);
		removeNotEmptyProduct(oldProduct);
		addProduct(newProduct);
	}
	/**
	 * Searches all the products for a given string. Search is case insensitive.
	 * Searches the name, price, and associated parts' names for possible matches.
	 * @param str the string to search for
	 * @return an observable list of products
	 */
	public ObservableList<Product> searchForProductByString(String str) {
		ObservableList<Product> productsFound = FXCollections.observableArrayList();
		
		String name = null;
		String price = null;
		
		// loop through all the products
		PRODUCTS:
		for(Product product : productList) {
			// get product properties to search on
			name = product.getName();
			price = PriceFormatter.format(product.getPrice());
			
			// loop through all the parts in that product
			for(Part part : product.getParts()) {
				// if the part name contains the search string, add the product
				// to the list and skip to the next product
				if(part.getName().toLowerCase().contains(str.toLowerCase())) {
					productsFound.add(product);
					continue PRODUCTS;
				}
			}
			
			// if there is a partial match in any of the search properties,
			// add the product to the list
			if(name.toLowerCase().contains(str.toLowerCase()) ||
				price.toLowerCase().contains(str.toLowerCase())) {
				productsFound.add(product);
			}
		}
		
		return productsFound;
	}
	
}
