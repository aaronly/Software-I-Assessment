package inventory;

import java.io.IOException;

import inventory.model.Inhouse;
import inventory.model.Inventory;
import inventory.model.Outsourced;
import inventory.model.Part;
import inventory.model.Product;
import inventory.view.MainScreenController;
import inventory.view.PartDialogController;
import inventory.view.ProductDialogController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InventoryApplication extends Application {

    private Stage primaryStage;
    private BorderPane root;
    private Inventory inventory;
    
    public InventoryApplication() {
    	inventory = new Inventory();
    	
    	try {
			// Sample Parts
    		inventory.addPart(new Inhouse("First Part", 1.50, 75, 10, 100, 546));
    		inventory.addPart(new Inhouse("Second Part", 3.75, 17, 5, 20, 112));
    		inventory.addPart(new Outsourced("Third Part", 10, 2, 0, 5, "ACME"));
    		inventory.addPart(new Outsourced("Fourth Part", 12.10, 4, 1, 10, "Brand X"));
    		inventory.addPart(new Inhouse("Fifth Part", 10, 46, 10, 100, 10101));
    		inventory.addPart(new Inhouse("Sixth Part", 50, 12, 5, 20, 112));
    		inventory.addPart(new Outsourced("Seventh Part", 25.64, 0, 0, 5, "ACME"));
    		inventory.addPart(new Outsourced("Eighth Part", .02, 7, 1, 10, "Brand X"));
    		inventory.addPart(new Outsourced("Ninth Part", 1, 264, 100, 1000, "Generic Industries"));
    		inventory.addPart(new Outsourced("Tenth Part", 2, 745, 100, 1000, "Generic Industries"));
    		
    		Product product1 = new Product("First Product", 500, 88, 10, 100, inventory.lookupPart("First Part"));
    		product1.addPart(inventory.lookupPart("Second Part"));
    		product1.addPart(inventory.lookupPart("Ninth Part"));
    		
    		Product product2 = new Product("Second Product", 250, 49, 10, 100, inventory.lookupPart("Sixth Part"));
    		product2.addPart(inventory.lookupPart("Sixth Part"));
    		product2.addPart(inventory.lookupPart("Sixth Part"));
    		product2.addPart(inventory.lookupPart("Sixth Part"));
    		product2.addPart(inventory.lookupPart("Sixth Part"));
    		
    		Product product3 = new Product("Third Product", 70, 12, 10, 100, inventory.lookupPart("Tenth Part"));
    		product3.addPart(inventory.lookupPart("Third Part"));
    		product3.addPart(inventory.lookupPart("Sixth Part"));
    		product3.addPart(inventory.lookupPart("Tenth Part"));
    		product3.addPart(inventory.lookupPart("First Part"));
    		
			// Sample Products
    		inventory.addProduct(product1);
    		inventory.addProduct(product2);
    		inventory.addProduct(product3);
    		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Set up the primary stage and initialize the root node
     */
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("WGU: Aaron Echols");
        this.primaryStage.getIcons().add(new Image("file:resources/main_icon.png"));
        
        initRootLayout();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
    
    /**
     * 
     * @return the main inventory
     */
    public Inventory getInventory() {
    	return inventory;
    }
    
	/**
	 * 
	 * @return the primary stage of the application
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
    /**
     * Initializes the root node.
     */
    public void initRootLayout() {
        try {
            // Load root node from main FXML file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InventoryApplication.class.getResource("view/MainScreen.fxml"));
            root = (BorderPane)loader.load();

            // Set up a scene with the root node and load that onto the primary stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            
            // Initialize the main controller and set up the main screen with a reference to the main application
            MainScreenController controller = loader.getController();
            controller.setMainScreen(this);
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Show the add/modify part dialog
     * @param part the part being modified, null is adding a new part
     */
    public void showPartDialog(Part part) {
    	// Change the title based on if the 'Add' or 'Modify' button was clicked
    	String title;
        if (part == null) { // new part
        	title = "Add a New Part";
        } else { // existing part
        	title = "Modify an Existing Part";
        }

    	try {
			// Load part dialog root node from part dialog FXML file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(InventoryApplication.class.getResource("view/PartDialog.fxml"));
			AnchorPane partDialogRoot = (AnchorPane) loader.load();

			// Create the part dialog stage
			Stage partDialogStage = new Stage();
			partDialogStage.setTitle(title);
			partDialogStage.initModality(Modality.WINDOW_MODAL);
			partDialogStage.initOwner(primaryStage);
			partDialogStage.getIcons().add(new Image("file:resources/part_icon.png"));
			
			// Set up a scene with the part dialog root node and load that onto the part dialog stage
			Scene scene = new Scene(partDialogRoot);
			partDialogStage.setScene(scene);
			
			// Initialize the part dialog controller and set up the dialog screen
			// with a reference to the part dialog stage, the inventory, and any part selected
			PartDialogController controller = loader.getController();
			controller.setDialogScreen(partDialogStage, inventory, part);
			
			partDialogStage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Show the add/modify product dialog
     * @param product the product being modified, null is adding a new product
     */
    public void showProductDialog(Product product) {
    	// Change the title based on if the 'Add' or 'Modify' button was clicked
    	String title;
        if (product == null) { // new product
        	title = "Add a New Product";
        } else { // existing product
        	title = "Modify an Existing Product";
        }

    	try {
    		// Load product dialog root node from product dialog FXML file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(InventoryApplication.class.getResource("view/ProductDialog.fxml"));
			AnchorPane productDialogRoot = (AnchorPane) loader.load();

			// Create the product dialog stage
			Stage productDialogStage = new Stage();
			productDialogStage.setTitle(title);
			productDialogStage.initModality(Modality.WINDOW_MODAL);
			productDialogStage.initOwner(primaryStage);
			productDialogStage.getIcons().add(new Image("file:resources/product_icon.png"));
			
			// Set up a scene with the product dialog root node and load that onto the product dialog stage
			Scene scene = new Scene(productDialogRoot);
			productDialogStage.setScene(scene);
			
			// Initialize the product dialog controller and set up the dialog screen
			// with a reference to the product dialog stage, the inventory, and any product selected
			ProductDialogController controller = loader.getController();
			controller.setDialogScreen(productDialogStage, inventory, product);
			
			productDialogStage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
