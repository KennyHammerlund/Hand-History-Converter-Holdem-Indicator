package HandHistoryConverter;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import handHistoryButtons.InputSelectBtn;
import handHistoryButtons.OutputSelectBtn;
import handHistoryButtons.openWindowBtn;
import handHistoryButtons.save;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Display extends Application implements Observer {
	public static Display instance;

	private Label inputLabel = new Label("Import File Name:");
	private Label input = new Label("Use button to select input database");
	private Label outputLabel = new Label("Output File Name:");
	private Label output = new Label("Use button to select output database");
	private Label status = new Label("");
	private Label saveStatus = new Label("File Not Set");
	private HandTable history = HandTable.instance();

	private Button inputBtn = new InputSelectBtn("Select Input");
	private Button outputBtn = new OutputSelectBtn("Select Output");
	private Button saveBtn = new save("Save");
	private Button openBtn = new openWindowBtn("Open Hand");

	private VBox outsideVBox = new VBox(10);

	private HBox inputLabelRow = new HBox(10);
	private HBox inputBtnRow = new HBox(10);
	private HBox outputLabelRow = new HBox(10);
	private HBox outputBtnRow = new HBox(10);

	private static Stage pStage = null;

	public static Stage getpStage() {
		return pStage;
	}

	private File inputFile;
	private File outputFile;

	public Display() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Hold'em Indicator to Poker Stars Hand History Converter");
		DataModel.instance().addObserver(this);
		pStage = primaryStage;

		// TableView Settings
		history.setItems(DataModel.instance().getHandList());
		TableColumn<Hand, String> idCol = new TableColumn<Hand, String>("Hand ID");
		idCol.setCellValueFactory(new PropertyValueFactory("ID"));

		TableColumn<Hand, String> timeCol = new TableColumn<Hand, String>("Time");
		timeCol.setPrefWidth(240);
		timeCol.setCellValueFactory(new PropertyValueFactory("time"));
		TableColumn<Hand, String> websiteCol = new TableColumn<Hand, String>("Website");
		websiteCol.setCellValueFactory(new PropertyValueFactory("website"));
		TableColumn<Hand, String> stakesCol = new TableColumn<Hand, String>("Stakes");
		stakesCol.setPrefWidth(120);
		stakesCol.setCellValueFactory(new PropertyValueFactory("stakes"));
		TableColumn<Hand, String> limitCol = new TableColumn<Hand, String>("Limit");
		limitCol.setCellValueFactory(new PropertyValueFactory("limit"));
		TableColumn<Hand, String> actionCol = new TableColumn<Hand, String>("Action");
		actionCol.setPrefWidth(200);
		actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
		TableColumn<Hand, Double> potCol = new TableColumn<Hand, Double>("Pot Size");
		potCol.setCellValueFactory(new PropertyValueFactory("pot"));
		TableColumn<Hand, Integer> buttonCol = new TableColumn<Hand, Integer>("Button");
		buttonCol.setCellValueFactory(new PropertyValueFactory("buttonPosition"));

		history.getColumns().setAll(idCol, timeCol, websiteCol, stakesCol, limitCol, actionCol, potCol, buttonCol);

		// openBtn Set to not active
		openBtn.setDisable(true);
		// set save button to not active
		saveBtn.setDisable(true);

		// add Elements to HBox
		outsideVBox.getChildren().addAll(history, inputLabelRow, inputBtnRow, outputLabelRow, outputBtnRow);

		// add Elements to VBox
		inputLabelRow.getChildren().addAll(inputLabel, input);
		inputBtnRow.getChildren().addAll(inputBtn, openBtn, status);
		outputLabelRow.getChildren().addAll(outputLabel, output);
		outputBtnRow.getChildren().addAll(outputBtn, saveBtn, saveStatus);

		// Create the Window
		Scene scene = new Scene(outsideVBox, 1000, 600);

		// Styling of The elements in the window
		// CSS Imports and class assignment
		scene.getStylesheets().add(getClass().getResource("fxStyle.css").toExternalForm());
		inputBtn.getStyleClass().add("btn");
		outputBtn.getStyleClass().add("btn");
		saveBtn.getStyleClass().add("btn");
		inputLabelRow.getStyleClass().add("row");
		inputBtnRow.getStyleClass().add("row");
		outputLabelRow.getStyleClass().add("row");
		outputBtnRow.getStyleClass().add("row");
		inputLabel.getStyleClass().add("boldLabel");
		outputLabel.getStyleClass().add("boldLabel");

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * On update change labels for the file names.
	 */
	@Override
	public void update(Observable o, Object arg) {
		inputFile = DataModel.instance().getInputFile();
		outputFile = DataModel.instance().getOutputFile();

		if (inputFile != null) {
			input.setText(inputFile.toString());
		}

		if (outputFile != null) {
			output.setText(outputFile.toString());
		}

		if (arg != null && arg instanceof boolean[]) {
			boolean[] bools = (boolean[]) arg;

			openBtn.setDisable(bools[0]);
			saveBtn.setDisable(bools[1]);
		}

		if (arg != null && arg instanceof String[]) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					String[] strings = (String[]) arg;
					status.setText(strings[0]);
					saveStatus.setText(strings[1]);
				}
			});
		}

	}
}