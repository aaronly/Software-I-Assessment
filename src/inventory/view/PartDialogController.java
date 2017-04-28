package inventory.view;

import java.text.ParseException;
import java.util.Optional;

import inventory.model.Inhouse;
import inventory.model.Inventory;
import inventory.model.Outsourced;
import inventory.model.Part;
import inventory.util.PriceFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class PartDialogController {

	private Stage dialogStage;
	private boolean isNewPart;
	private Inventory inventory;
	private Part part;

	@FXML
	private Label titleLabel;
	@FXML
	private RadioButton inhouseRadio;
	@FXML
	private RadioButton outsourcedRadio;
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
	private Label sourceLabel;
	@FXML
	private TextField sourceField;
	
	/**
	 * Constructor is called first
	 */
	public PartDialogController() {};
	
	/**
	 * After the constructor is called, the initialize method is called automatically
	 */
	@FXML
	private void initialize() {
		// Create a new toggle group
		ToggleGroup radioGroup = new ToggleGroup();
		
		// add the radio buttons to the toggle group
		inhouseRadio.setToggleGroup(radioGroup);
		outsourcedRadio.setToggleGroup(radioGroup);
		
		// set up the listener for the radio buttons
		radioGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> sourceChanged(newToggle));
		
		// set up a listener on the price field to add the dollar symbol
		priceField.textProperty().addListener((obs, oldStr, newStr) -> {
			if (newStr == null || newStr.isEmpty()) return;
			if (!newStr.contains("$")) {
				priceField.setText("$" + newStr);
			}
		});
	}
	
	/**
	 * Set up the stage after initialization
	 * @param stage the stage of the dialog
	 * @param existingInventory the existing inventory that holds the part being edited
	 * @param editPart the part being modified, null if a part is being added
	 */
	public void setDialogScreen(Stage stage, Inventory existingInventory, Part editPart) {
		dialogStage = stage;
		inventory = existingInventory;
		part = editPart;
		
        if (editPart == null) { // new part
        	isNewPart = true;
        	titleLabel.setText("Add Part");
        } else { // existing part
        	isNewPart = false;
        	titleLabel.setText("Modify Part");
        	setPartInfo(editPart);
        }
	}
	
	/**
	 * Initialize all of the text fields if an existing part is being modified.
	 * @param part the part being modified
	 */
	public void setPartInfo(Part part) {
		if (part instanceof Inhouse) {
			inhouseRadio.setSelected(true);
			sourceField.setText(Integer.toString(((Inhouse)part).getMachineID()));
			sourceLabel.setText("Machine ID");
		} else if (part instanceof Outsourced) {
			outsourcedRadio.setSelected(true);
			sourceField.setText(((Outsourced)part).getCompanyName());
			sourceLabel.setText("Company Name");
		}
		
		idField.setText(Integer.toString(part.getPartID()));
		nameField.setText(part.getName());
		instockField.setText(Integer.toString(part.getInstock()));
		minField.setText(Integer.toString(part.getMin()));
		maxField.setText(Integer.toString(part.getMax()));
		priceField.setText(PriceFormatter.format(part.getPrice()));
	}
	
	/**
	 * Change dialog labels and text fields if the radio button selection changes
	 * @param toggle the toggle that was newly selected
	 */
	private void sourceChanged(Toggle toggle) {

		String labelText = "";
		
		if (toggle.equals(inhouseRadio)) {
			labelText = "Machine ID";
		} else if (toggle.equals(outsourcedRadio)) {
			labelText = "Company Name";
		}
		sourceLabel.setText(labelText);
		
		if (sourceField.getText().isEmpty()) {
			sourceField.setPromptText(labelText);
		}
	}
	
	/**
	 * Update the inventory with the part as shown in the dialog. If adding a part,
	 * there will be a confirmation if a part with an existing name already exists in the
	 * inventory. This will keep ID numbers consistent.
	 */
	@FXML
	private void handleSave() {
		Part editPart = null;
		boolean isInhouse = inhouseRadio.isSelected();
		
		// Initialize warning dialog (may not be needed)
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(dialogStage);
		alert.setTitle("Part Not Saved!");
		
		// Get all the values from the dialog box
		String name = nameField.getText();
		String instockString = instockField.getText();
		String priceString = priceField.getText();
		String maxString = maxField.getText();
		String minString = minField.getText();
		String sourceString = sourceField.getText();
		
		try {
			// make sure that all text fields have a value
			if(name.isEmpty() || instockString.isEmpty() ||
				priceString.isEmpty() || maxString.isEmpty() ||
				minString.isEmpty() || sourceString.isEmpty()) {
				throw new IllegalArgumentException("All text fields must have a value.");
			}
			
			// try parsing all of the numeric values
			double price = PriceFormatter.parse(priceString);
			int instock = Integer.parseInt(instockString);
			int min = Integer.parseInt(minString);
			int max = Integer.parseInt(maxString);
			
			// declare and initialize both variables for source
			String companyName = null;
			int machineID = -1;

			if (isInhouse) { // if the part is made in house
				machineID = Integer.parseInt(sourceString);
				editPart = new Inhouse(name, price, instock, min, max, machineID);
			} else { // if the part is outsourced
				companyName = sourceString;
				editPart = new Outsourced(name, price, instock, min, max, companyName);
			}
			
			if(isNewPart) { // a new part is being added
				// see if a part with the same name already exists
				Part searchPart = inventory.lookupPart(editPart.getName());
				// if it doesn't exist in the inventory yet OR
				// it does exist, but the user wants to add it anyway
				if (searchPart == null || existingPartFound() == ButtonType.YES) {
					inventory.addPart(editPart); // add the part to the inventory
				}
			} else { // an existing part is being modified
				inventory.updatePart(part, editPart);
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
	 * Cancel the add/modify part dialog with confirmation
	 */
	@FXML
	private void handleCancel() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit?");
		if (isNewPart) {
			alert.setContentText("Exit without saving new part?");
		} else {
			alert.setContentText("Exit without updating part?");
		}
		alert.setHeaderText("Part Not Saved!");
		alert.initOwner(dialogStage);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			dialogStage.close();
		}
	}
	
	/**
	 * A dialog notifying the user that a part with that name already exists in the inventory.
	 * @return YES if the user wants to add the part anyways.
	 * NO or null if the user clicked NO or closed the dialog, respectively.
	 */
	private ButtonType existingPartFound() {
		ButtonType result = ButtonType.NO;
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(dialogStage);
		alert.setTitle("Duplicate Part Found");
		alert.setHeaderText("Add duplicate part?");
		alert.setContentText("A part with that name already exists in the inventory. \n"
				+ "Do you want to add this part?");
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> answer = alert.showAndWait();
		result = answer.get();
		
		return result;
	}

}
