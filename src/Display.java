import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Display extends Application {
	public static Display instance;
	private static Label tableLabel = new Label("Hand Histories From Holdem Indicator");
	private static Label inputLabel = new Label("Import File Name:");
	private static Label input = new Label("Use button to select input database");
	private static Label outputLabel = new Label("Output File Name:");
	private static Label output = new Label("Use button to select output database");
	private static TableView history = new TableView();

	private static Button inputBtn = new Button("Select Input");
	private static Button outputBtn = new Button("Select Ouput");
	private static Button importBtn = new Button("Import");
	private static Button saveBtn = new Button("Save");

	private static VBox outsideVBox = new VBox(10);

	private static HBox inputLabelRow = new HBox(10);
	private static HBox inputBtnRow = new HBox(10);
	private static HBox outputLabelRow = new HBox(10);
	private static HBox outputBtnRow = new HBox(10);

	public Display() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Hold'em Indicator to Poker Stars Hand History Converter");

		// add Elements to HBox
		outsideVBox.getChildren().addAll(tableLabel, history, inputLabelRow, inputBtnRow, outputLabelRow, outputBtnRow);

		// add Elements to VBox
		inputLabelRow.getChildren().addAll(inputLabel, input);
		inputBtnRow.getChildren().addAll(inputBtn, importBtn);
		outputLabelRow.getChildren().addAll(outputLabel, output);
		outputBtnRow.getChildren().addAll(outputBtn, saveBtn);

		// Create the Window
		Scene scene = new Scene(outsideVBox, 800, 600);

		// Styling of The elements in the window
		// CSS Imports and class assignment
		scene.getStylesheets().add(getClass().getResource("fxStyle.css").toExternalForm());
		inputBtn.getStyleClass().add("btn");
		outputBtn.getStyleClass().add("btn");
		importBtn.getStyleClass().add("btn");
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
}