package inventory.view;

import java.text.ParseException;
import java.util.Optional;

import inventory.model.Inventory;
import inventory.model.Part;
import inventory.model.Product;
import inventory.util.CurrencyCell;
import inventory.util.PriceFormatter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProductDialogController {

	private Stage dialogStage;
	private boolean isNewProduct;
	private Inventory inventory;
	private Product product;
	private ObservableList<Part> parts;
	
	@FXML
	private Label titleLabel;
	@FXML
	private TextField idField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField instockField;
	@FXML
	private TextField priceField;
	@FXML
	private TextField maxField;
	@FXML
	private TextField minField;
	
	@FXML
	private TextField searchField;
	
	@FXML
	private TableView<Part> addPartTable;
	@FXML
	private TableColumn<Part, Integer> addPartIDColumn;
	@FXML
	private TableColumn<Part, String> addPartNameColumn;
	@FXML
	private TableColumn<Part, Integer> addPartQTYColumn;
	@FXML
	private TableColumn<Part, Double> addPartPriceColumn;
	
	@FXML
	private TableView<Part> delPartTable;
	@FXML
	private TableColumn<Part, Integer> delPartIDColumn;
	@FXML
	private TableColumn<Part, String> delPartNameColumn;
	@FXML
	private TableColumn<Part, Integer> delPartQTYColumn;
	@FXML
	private TableColumn<Part, Double> delPartPriceColumn;
	
	@FXML
	private Button addButton;
	@FXML
	private Button deleteButton;

	/**
	 * Constructor is called first
	 */
	public ProductDialogController() {};
	
	/**
	 * After the constructor is called, the initialize method is called automatically
	 */
	@FXML
	private void initialize() {
		
		// Initialize the Add Part Table Columns
		addPartIDColumn.setCellValueFactory(cell -> cell.getValue().partIDProperty().asObject());
		addPartNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		addPartQTYColumn.setCellValueFactory(cell -> cell.getValue().instockProperty().asObject());
		addPartPriceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
		addPartPriceColumn.setCellFactory(param -> new CurrencyCell<Part>());
		
		// Initialize the Delete Part Table Columns
		delPartIDColumn.setCellValueFactory(cell -> cell.getValue().partIDProperty().asObject());
		delPartNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		delPartQTYColumn.setCellValueFactory(cell -> cell.getValue().instockProperty().asObject());
		delPartPriceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
		delPartPriceColumn.setCellFactory(param -> new CurrencyCell<Part>());
		
		// add the listeners to the search field so that if it is empty, reset view to all parts
		// optionally, the search button can be eliminated by un-commenting the "else" block
		searchField.textProperty().addListener((obs, oldText, newText) -> {
			if (newText == null || newText.isEmpty()) {
				addPartTable.setItems(inventory.getPartList());
			}//else { partTable.setItems(inventory.searchForPartByString(newText)); }
		});
		
		// set up a listener on the price field to add the dollar symbol
		priceField.textProperty().addListener((obs, oldStr, newStr) -> {
			if (newStr == null || newStr.isEmpty()) return;
			if (!newStr.contains("$")) {
				priceField.setText("$" + newStr);
			}
		});
		
		// disable the buttons that cannot operate on zero selections
		addButton.setDisable(true);
		deleteButton.setDisable(true);
		
		// add listeners to the tables to disable the buttons if there are no selections
		addPartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> 
				addButton.setDisable(newSel == null));
		delPartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> 
				deleteButton.setDisable(newSel == null));
	}
	
	/**
	 * Set up the stage after initialization
	 * @param stage the stage of the dialog
	 * @param existingInventory the existing inventory that holds the product being edited
	 * @param editProduct the product being modified, null if a product is being added
	 */
	public void setDialogScreen(Stage stage, Inventory existingInventory, Product editProduct) {
		dialogStage = stage;
		inventory = existingInventory;
		product = editProduct;
		
		addPartTable.setItems(inventory.getPartList());
		
        if (editProduct == null) { // new part
        	isNewProduct = true;
        	titleLabel.setText("Add Product");
        } else { // existing part
        	isNewProduct = false;
        	titleLabel.setText("Modify Product");
        	setProductInfo(editProduct);
        }
	}
	
	/**
	 * Initialize all of the text fields and part list if an existing product is being modified.
	 * @param product the product being modified
	 */
	public void setProductInfo(Product product) {
		idField.setText(Integer.toString(product.getProductID()));
		nameField.setText(product.getName());
		instockField.setText(Integer.toString(product.getInstock()));
		minField.setText(Integer.toString(product.getMin()));
		maxField.setText(Integer.toString(product.getMax()));
		priceField.setText(PriceFormatter.format(product.getPrice()));
		
		delPartTable.setItems(product.getParts());
	}
	
	/**
	 * Search for a given part based on the text in the search field.
	 * Search parameters defined in the {@link inventory.model.Inventory} class.
	 */
	@FXML
	private void handleSearch() {
		String searchText = searchField.getText();
		if(searchText == null || searchText.isEmpty()) {
			addPartTable.setItems(inventory.getPartList());
		}
		
		ObservableList<Part> partsFound = inventory.searchForPartByString(searchText);
		addPartTable.setItems(partsFound);
	}
	
	/**
	 * Add a part to the list of contained parts. This may also update the price field
	 * if the price would be less than the sum of the parts's price.
	 */
	@FXML
	private void handleAdd() {
		Part partToAdd = addPartTable.getSelectionModel().getSelectedItem();
		
		try {
			// get the price of the part being added
			double price = 0;
			String priceString = priceField.getText();
			if(priceString != null && !priceString.isEmpty()) {
				price = PriceFormatter.parse(priceString);
			}
			
			// add the part to the table
			delPartTable.getItems().add(partToAdd);
			
			// get the total cost of all the parts
			double partCost = getCostOfParts();
			
			// make sure that the total price will be greater than the sum of the parts
			if (partCost > price) {
				price += partToAdd.getPrice();
				priceString = PriceFormatter.format(price);
				priceField.setText(priceString);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Delete a part to the list of contained parts. This also updates the price field
	 * unless the price would fall below zero.
	 */
	@FXML
	private void handleDelete() {
		Part partToDelete = delPartTable.getSelectionModel().getSelectedItem();
		
		// get all the existing parts in the contained parts table
		delPartTable.getItems().remove(partToDelete);
		
		try {
			// get the price of the part being added
			double price = 0;
			String priceString = priceField.getText();
			if(priceString != null && !priceString.isEmpty()) {
				price = PriceFormatter.parse(priceString);
			}
			
			// make sure that the total price will stay positive
			if (price - partToDelete.getPrice() >= 0) {
				// subtract part price from product price
				price -= partToDelete.getPrice();
				priceString = PriceFormatter.format(price);
				priceField.setText(priceString);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the inventory with the product as shown in the dialog. If adding a product,
	 * there will be a confirmation if a product with an existing name already exists in the
	 * inventory. This will keep ID numbers consistent.
	 */
	@FXML
	private void handleSave() {
		Product editProduct = null;
		
		// Initialize warning dialog (may not be needed)
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(dialogStage);
		alert.setTitle("Product Not Saved!");
		
		// Get all the values from the dialog box
		String name = nameField.getText();
		String instockString = instockField.getText();
		String priceString = priceField.getText();
		String maxString = maxField.getText();
		String minString = minField.getText();
		
		// Get all the parts from the containing table
		parts = delPartTable.getItems();

		try {
			// make sure that all text fields have a value
			if(name.isEmpty() || instockString.isEmpty() ||
				priceString.isEmpty() || maxString.isEmpty() ||
				minString.isEmpty() ) {
				throw new IllegalArgumentException("All text fields must have a value.");
			}
			
			// make sure there is at least one part
			if (parts.isEmpty()) {
				throw new IllegalArgumentException("Products must contain at least one part.");
			}
			
			// try parsing all of the numeric values
			double price = PriceFormatter.parse(priceString);
			int instock = Integer.parseInt(instockString);
			int min = Integer.parseInt(minString);
			int max = Integer.parseInt(maxString);
			
			// get the total cost of all the parts
			double partCost = getCostOfParts();
			
			// check that the total cost of the parts is less than the price
			if (partCost > price) {
				throw new IllegalArgumentException("Price cannot be less than the total\n"
						+ "price of all the parts it contains.");
			}
			
			editProduct = new Product(name, price, instock, min, max, parts);
			
			if(isNewProduct) { // a new product is being added
				// see if a product with the same name already exists
				Product searchProduct = inventory.lookupProduct(editProduct.getName());
				// if it doesn't exist in the inventory yet OR
				// it does exist, but the user wants to add it anyway
				if (searchProduct == null || existingProductFound() == ButtonType.YES) {
					inventory.addProduct(editProduct); // add the product to the inventory
				}
			} else { // an existing product is being modified
				inventory.updateProduct(product, editProduct);
			}
			
			
		} catch (NumberFormatException e) {
			alert.setContentText("There was an invalid number in one of the fields. \n"
					+ "Please try again.");
			alert.showAndWait();
			return;
		} catch (IllegalArgumentException e) {
			alert.setContentText(e.getMessage());;
			alert.showAndWait();
			return;
		} catch (ParseException e) {
			alert.setContentText("That is not a valid price. \n"
					+ "Please try again.");
			alert.showAndWait();
			return;
		}
		dialogStage.close();
	}
	/**
	 * Cancel the add/modify product dialog with confirmation
	 */
	@FXML
	private void handleCancel() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit?");
		if (isNewProduct) {
			alert.setContentText("Exit without saving new product?");
		} else {
			alert.setContentText("Exit without updating product?");
		}
		alert.setHeaderText("Product Not Saved!");
		alert.initOwner(dialogStage);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			dialogStage.close();
		}
	}

	/**
	 * 
	 * @return the total cost of all parts contained in the product
	 * as currently shown in the table view
	 */
	private double getCostOfParts() {
		double partCost = 0;
		
		// get all the parts from the table
		ObservableList<Part> partsContained = delPartTable.getItems();
		
		// make sure there is at least one part or return zero
		if (partsContained == null || partsContained.isEmpty()) {
			return partCost;
		}
		
		// get the total sum cost
		for(Part part : partsContained) {
			partCost += part.getPrice();
		}
		return partCost;
	}

	/**
	 * A dialog notifying the user that a product with that name already exists in the inventory.
	 * @return YES if the user wants to add the product anyways.
	 * NO or null if the user clicked NO or closed the dialog, respectively.
	 */
	private ButtonType existingProductFound() {
		ButtonType result = ButtonType.NO;
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(dialogStage);
		alert.setTitle("Duplicate Product Found");
		alert.setHeaderText("Add duplicate product?");
		alert.setContentText("A product with that name already exists in the inventory. \n"
				+ "Do you want to add this product?");
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> answer = alert.showAndWait();
		result = answer.get();
		
		return result;
	}
}
