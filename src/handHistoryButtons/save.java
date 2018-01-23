package handHistoryButtons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import HandHistoryConverter.DataModel;
import HandHistoryConverter.FileIO;
import HandHistoryConverter.Hand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class save extends Button {

	public save(String string) {
		this.setText(string);
		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// get file to write
				File file = DataModel.instance().getOutputFile();
				// create list of hand histories
				List<String> handStringList = new ArrayList<String>();
				for (Hand h : DataModel.instance().getHandList()) {
					handStringList.add(h.getAction());
				}
				System.out.println("HSL Created..");
				// write to file
				if (FileIO.writeFile(file, handStringList)) {
					DataModel.instance().setSaveText("Save Complete!");
				}
			}
		});
	}
}
