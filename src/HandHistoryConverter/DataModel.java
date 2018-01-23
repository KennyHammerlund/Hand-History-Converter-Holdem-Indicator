
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
	private String[] statusText = { "Select A File", "File Not Set" };
	private boolean[] btnDisable = { true, true };

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
	 * Sets the save button value to disabled or enabled based on if the files
	 * are set
	 */
	public void saveButtonChanger() {
		if (this.inputFile != null && this.outputFile != null) {
			this.btnDisable[1] = false;
		} else {
			this.btnDisable[1] = true;
		}
		setChanged();
		notifyObservers(this.btnDisable);
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
		return statusText[0];
	}

	public void setBtnRunText(String btnRunText) {
		this.statusText[0] = btnRunText;
		setChanged();
		notifyObservers(this.statusText);
	}

	public String getSaveText() {
		return statusText[1];
	}

	public void setSaveText(String saveText) {
		this.statusText[1] = saveText;
		setChanged();
		notifyObservers(this.statusText);

	}

	public boolean isBtnDisable() {
		return this.btnDisable[0];
	}

	public void setBtnDisable(boolean btnDisable) {
		this.btnDisable[0] = btnDisable;
		setChanged();
		notifyObservers(this.btnDisable);
	}

	public boolean isSaveBtnDisable() {
		return this.btnDisable[1];
	}

	public void setSaveBtnDisable(boolean saveBtnDisable) {
		this.btnDisable[1] = saveBtnDisable;
		setChanged();
		notifyObservers(btnDisable);
	}

}
