package handHistoryButtons;

import java.io.File;

import HandHistoryConverter.DataModel;
import HandHistoryConverter.Display;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class save extends Button {

	public save(String string) {
		this.setText(string);
		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Database Files", "*.db"),
						new ExtensionFilter("All Files", "*.*"));
				File selectedFile = fileChooser.showOpenDialog(Display.getpStage());
				DataModel.instance().setOutputFile(selectedFile);
			}

		});
	}
}
