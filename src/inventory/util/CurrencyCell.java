package inventory.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.UnaryOperator;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
/**
 * Class to control the display of currency value in a table view.
 * 
 * I'm not sure how to cite a reference to some code from another author.
 * Since the display of data is not a requirement to pass the assessment,
 * I hope this citation is sufficient.
 * @author James_D (Co-director at Marshall University Genomics and Bioinformatics Core Facility)
 * @see <a href="https://stackoverflow.com/users/2189127/james-d">James_D</a>
 * @see <a href="https://stackoverflow.com/questions/36087968/adding-currency-symbol-to-tableview-but-remove-on-edit-cell">Adding currency symbol to tableview...</a>
 *
 * @param <T> The generic object contained in the table cell.
 */
public class CurrencyCell<T> extends TableCell<T, Double> {

    private final TextField textField;
    private final NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance();
    private final DecimalFormat textFieldFormat = new DecimalFormat("0.00");

    public CurrencyCell() {
        this.textField = new TextField();
        
        // create the converter
        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public String toString(Double dbl) {
            	String result = "";
            	if (dbl != null) {
            		result = textFieldFormat.format(dbl);
            	}
                return result;
            }

            @Override
            public Double fromString(String string) {
                Double result = new Double(0);
            	try {
                	if (!string.isEmpty()) {
                		result = textFieldFormat.parse(string).doubleValue();
                	}
                    return result;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0.0 ;
                }
            }

        };
        
        // create the filter
        UnaryOperator<Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change ;
            }
            try {
                textFieldFormat.parse(newText);
                return change ;
            } catch (ParseException e) {
                return null ;
            }
        };
        
        // create the actual text formatter
        TextFormatter<Double> textFormatter = new TextFormatter<Double>(converter, 0.0, filter);
        
        textField.setTextFormatter(textFormatter);

        textField.setOnAction(e -> commitEdit(converter.fromString(textField.getText())));
        
        textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else if (isEditing()) {
            textField.setText(item.toString());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(currencyFormat.format(item));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(textFieldFormat.format(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(currencyFormat.format(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void commitEdit(Double newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }
}