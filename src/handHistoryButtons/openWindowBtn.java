package handHistoryButtons;

import HandHistoryConverter.Hand;
import HandHistoryConverter.HandTable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
		Label mainLabel = new Label("Information for hand " + hand.getID());
		Label timeLabel = new Label("Time: " + hand.getTime());
		Label webLabel = new Label("Website: " + hand.getWebsite());
		Label stakeLabel = new Label("Stakes: " + hand.getStakes());
		Label potLabel = new Label("Pot Size: $" + hand.getPot());
		Label actLabel = new Label("Action:");
		TextArea actionText = new TextArea();
		actionText.setEditable(false);
		actionText.setText(hand.getAction());
		actionText.setPadding(ten);
		actionText.setPrefHeight(600);

		VBox root = new VBox();

		// add Labels
		root.getChildren().addAll(mainLabel, timeLabel, webLabel, stakeLabel, potLabel, actLabel);
		root.getChildren().add(actionText);
		Stage stage = new Stage();

		stage.setTitle("My New Stage Title");
		Scene scene = new Scene(root, 450, 800);

		// Style
		// scene.getStylesheets().add(getClass().getResource("fxStyle.css").toExternalForm());
		root.setPadding(ten);
		stage.setScene(scene);
		stage.show();
	}
}
