package handHistoryButtons;

import HandHistoryConverter.Hand;
import HandHistoryConverter.HandTable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class openWindowBtn extends Button {

	public openWindowBtn(String string) {
		this.setText(string);
		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				newWindow(HandTable.instance().getSelectionModel().getSelectedItem());
			}
		});
	}

	/**
	 * Opens new window with targeted information
	 */
	public void newWindow(Hand hand) {

		Insets ten = new Insets(10, 10, 10, 10);
		TextArea actionText = new TextArea();
		actionText.setEditable(false);
		actionText.setText(hand.getAction());
		actionText.setPadding(ten);
		actionText.setPrefHeight(600);

		// Copy to Clipboard Button
		copyClipboardBtn ccb = new copyClipboardBtn("Copy to Clipboard");
		ccb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ccb.copyClip(actionText.getText());
			}
		});
		HBox btnBox = new HBox(ccb);
		btnBox.setAlignment(Pos.CENTER_RIGHT);
		btnBox.setPadding(ten);

		VBox root = new VBox(actionText, btnBox);

		Stage stage = new Stage();

		stage.setTitle("Information for hand " + hand.getID());
		Scene scene = new Scene(root, 450, 650);

		// Style
		// scene.getStylesheets().add(getClass().getResource("fxStyle.css").toExternalForm());
		root.setPadding(ten);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}
}
