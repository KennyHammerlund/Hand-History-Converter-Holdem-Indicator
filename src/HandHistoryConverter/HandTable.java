package HandHistoryConverter;

import javafx.scene.control.TableView;

public class HandTable extends TableView<Hand> {

	private static HandTable instance;

	public HandTable() {

	}

	/**
	 * Create Singleton
	 * 
	 * @return instance of the DataModel
	 */
	public static HandTable instance() {
		if (instance == null) {
			instance = new HandTable();
		}
		return instance;
	}

}
