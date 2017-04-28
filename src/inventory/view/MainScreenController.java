package inventory.view;

import java.util.Optional;

import inventory.InventoryApplication;
import inventory.model.Inventory;
import inventory.model.Part;
import inventory.model.Product;
import inventory.util.CurrencyCell;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainScreenController {

	private InventoryApplication mainApp;
	private Inventory inventory;
	
	@FXML
	private TableView<Part> partTable;
	@FXML
	private TableColumn<Part, Integer> partIDColumn;
	@FXML
	private TableColumn<Part, String> partNameColumn;
	@FXML
	private TableColumn<Part, Integer> partQTYColumn;
	@FXML
	private TableColumn<Part, Double> partPriceColumn;
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, Integer> productIDColumn;
	@FXML
	private TableColumn<Product, String> productNameColumn;
	@FXML
	private TableColumn<Product, Integer> productQTYColumn;
	@FXML
	private TableColumn<Product, Double> productPriceColumn;
	
	@FXML
	private Button modifyPartButton;
	@FXML
	private Button deletePartButton;
	@FXML
	private Button modifyProductButton;
	@FXML
	private Button deleteProductButton;
	@FXML
	private TextField searchPartField;
	@FXML
	private TextField searchProductField;

	/**
	 * Constructor is called first
	 */
	public MainScreenController() {};
	
	/**
	 * After the constructor is called, the initialize method is called automatically
	 */
	@FXML
	private void initialize() {
		
		// Initialize the Part Table Columns
		partIDColumn.setCellValueFactory(cell -> cell.getValue().partIDProperty().asObject());
		partNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		partQTYColumn.setCellValueFactory(cell -> cell.getValue().instockProperty().asObject());
		partPriceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
		partPriceColumn.setCellFactory(param -> new CurrencyCell<Part>());

		// Initialize the Product Table Columns
		productIDColumn.setCellValueFactory(cell -> cell.getValue().productIDProperty().asObject());
		productNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		productQTYColumn.setCellValueFactory(cell -> cell.getValue().instockProperty().asObject());
		productPriceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
		productPriceColumn.setCellFactory(param -> new CurrencyCell<Product>());
		
		// disable the buttons that cannot operate on zero selections
		changeButtonState(partTable, true);
		changeButtonState(productTable, true);
		
		// add listeners to the tables to disable the buttons if there are no selections
		partTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> 
				changeButtonState(partTable, newSel == null));
		productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> 
				changeButtonState(productTable, newSel == null));
		
		// add the listeners to the search fields so that if it is empty, reset view to all parts/products
		// optionally, the search button can be eliminated by un-commenting the "else" blocks
		searchPartField.textProperty().addListener((obs, oldText, newText) -> {
			if (newText == null || newText.isEmpty()) {
				partTable.setItems(inventory.getPartList());
			}//else { partTable.setItems(inventory.searchForPartByString(newText)); }
		});
		searchProductField.textProperty().addListener((obs, oldText, newText) -> {
			if (newText == null || newText.isEmpty()) {
				productTable.setItems(inventory.getProductList());
			}//else { productTable.setItems(inventory.searchForProductByString(newText)); }
		});
	}
	
	/**
	 * Initialize the main screen is the parts and products currently in the inventory
	 * @param app
	 */
	public void setMainScreen(InventoryApplication app) {
		mainApp = app;
		inventory = app.getInventory();
		
		partTable.setItems(inventory.getPartList());
		productTable.setItems(inventory.getProductList());
	}
	
	/**
	 * Change the state of the modify and delete buttons if nothing is selected.
	 * @param table the TableView which has no selections
	 * @param state true to enable the buttons, false to disable
	 */
	private void changeButtonState(TableView<?> table, boolean state) {
		if (table.equals(partTable)) {
			modifyPartButton.setDisable(state);
			deletePartButton.setDisable(state);
		} else if (table.equals(productTable)) {
			modifyProductButton.setDisable(state);
			deleteProductButton.setDisable(state);
		}
	}

	/**
	 * Exit the application with confirmation dialog.
	 */
	@FXML
	private void handleExit() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit?");
		alert.setHeaderText("Are you sure you want to quit?");
		alert.initOwner(mainApp.getPrimaryStage());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			Platform.exit();
		}
	}
	
    // ------------------------ PART METHODS ------------------------ 
	
	/**
	 * Show the add/modify part dialog with a null parameter.
	 * This signifies that a new part is being added.
	 */
	@FXML
	private void handleAddPart() {
		mainApp.showPartDialog(null);
	}
	/**
	 * Show the add/modify part dialog with the part selected passed as a parameter.
	 * This signifies that the selected part is being modified.
	 */
	@FXML
	private void handleModifyPart() {
		Part existingPart = partTable.getSelectionModel().getSelectedItem();
		mainApp.showPartDialog(existingPart);
	}
	/**
	 * Search for a given part based on the text in the search field.
	 * Search parameters defined in the {@link inventory.model.Inventory} class.
	 */
	@FXML
	private void handleSearchPart() {
		String searchText = searchPartField.getText();
		if(searchText == null || searchText.isEmpty()) {
			partTable.setItems(inventory.getPartList());
		}
		
		ObservableList<Part> partsFound = inventory.searchForPartByString(searchText);
		partTable.setItems(partsFound);
	}
	/**
	 * Delete a part from the inventory with confirmation dialog.
	 */
	@FXML
	private void handleDeletePart() {
		Part part = partTable.getSelectionModel().getSelectedItem();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete?");
		alert.setHeaderText("Are you sure you want to delete " + part.getName() + "?");
		alert.initOwner(mainApp.getPrimaryStage());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			inventory.removePart(part);
		}
	}
	
	// ------------------------ PRODUCT METHODS ------------------------ 
	
	/**
	 * Show the add/modify product dialog with a null parameter.
	 * This signifies that a new product is being added.
	 */
	@FXML
	private void handleAddProduct() {
		mainApp.showProductDialog(null);
	}
	/**
	 * Show the add/modify product dialog with the product selected passed as a parameter.
	 * This signifies that the selected product is being modified.
	 */
	@FXML
	private void handleModifyProduct() {
		Product existingProduct = productTable.getSelectionModel().getSelectedItem();
		mainApp.showProductDialog(existingProduct);
	}
	/**
	 * Search for a given product based on the text in the search field.
	 * Search parameters defined in the {@link inventory.model.Inventory} class.
	 */
	@FXML
	private void handleSearchProduct() {
		String searchText = searchProductField.getText();
		if(searchText == null || searchText.isEmpty()) {
			productTable.setItems(inventory.getProductList());
		}
		
		ObservableList<Product> productsFound = inventory.searchForProductByString(searchText);
		productTable.setItems(productsFound);
	}
	/**
	 * Delete a product from the inventory with confirmation dialog.
	 * If the product contains one or more parts a second warning asks whether or not to proceed.
	 */
	@FXML
	private void handleDeleteProduct() {
		Product product = productTable.getSelectionModel().getSelectedItem();
		
		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("Delete?");
		confirmation.setHeaderText("Are you sure you want to delete " + product.getName() + "?");
		confirmation.initOwner(mainApp.getPrimaryStage());
		Optional<ButtonType> confirmResult = confirmation.showAndWait();
		
		try {
			if (confirmResult.get() == ButtonType.OK){
				inventory.removeProduct(product);
			}
		} catch (Exception e) { // catch exception if product still contains parts
			Alert warning = new Alert(AlertType.WARNING);
			warning.setTitle("Product Not Empty!");
			warning.setContentText("This product still contains one or more parts. \n"
					+ "Are you sure you want to delete this product?");
			warning.getButtonTypes().clear();
			warning.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
			Optional<ButtonType> deleteResult = warning.showAndWait();
			
			if (deleteResult.get() == ButtonType.YES) {
				inventory.removeNotEmptyProduct(product);
			}
		}
	}
}
