package handHistoryButtons;

import java.io.File;

import HandHistoryConverter.DataModel;
import HandHistoryConverter.Display;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class OutputSelectBtn extends Button {

	public OutputSelectBtn(String string) {
		this.setText(string);
		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose Save Destination");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text File", "*.txt"),
						new ExtensionFilter("All Files", "*.*"));
				File selectedFile = fileChooser.showSaveDialog(Display.getpStage());
				DataModel.instance().setOutputFile(selectedFile);
				DataModel.instance().setSaveText("File Set: Not Saved");
				DataModel.instance().saveButtonChanger();
			}

		});
	}
}
