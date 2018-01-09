
package HandHistoryConverter;

import java.io.File;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model to store data of hand history program:
 * 
 * Version: Beta .1
 * 
 * @author Kenny Hammerlund
 *
 */
public class DataModel extends Observable {

	private static DataModel instance;
	private ObservableList<Hand> handList = FXCollections.observableArrayList();
	private File inputFile = null;
	private File outputFile = null;
	private String btnRunText = "Select A File";
	private boolean btnDisable = true;

	/**
	 * Create Singleton
	 * 
	 * @return instance of the DataModel
	 */
	public static DataModel instance() {
		if (instance == null) {
			instance = new DataModel();
		}
		return instance;
	}

	/**
	 * Adds hand to the observable list of hands
	 * 
	 * @param newHand
	 */
	public void addHand(Hand newHand) {
		handList.add(newHand);
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
		setChanged();
		notifyObservers();
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
		setChanged();
		notifyObservers();
	}

	public ObservableList<Hand> getHandList() {
		return handList;
	}

	public void setHandList(ObservableList<Hand> handList) {
		this.handList = handList;
	}

	public String getBtnRunText() {
		return btnRunText;
	}

	public void setBtnRunText(String btnRunText) {
		this.btnRunText = btnRunText;
		setChanged();
		notifyObservers(this.btnRunText);

	}

	public boolean isBtnDisable() {
		return btnDisable;
	}

	public void setBtnDisable(boolean btnDisable) {
		this.btnDisable = btnDisable;
		setChanged();
		notifyObservers();
	}

}
