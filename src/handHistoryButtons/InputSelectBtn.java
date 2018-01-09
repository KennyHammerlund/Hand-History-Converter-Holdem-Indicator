package handHistoryButtons;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import HandHistoryConverter.DataModel;
import HandHistoryConverter.Display;
import HandHistoryConverter.FileIO;
import HandHistoryConverter.Hand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class InputSelectBtn extends Button {

	public InputSelectBtn(String string) {
		this.setText(string);
		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String path = System.getenv("SystemDrive");
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Database Files", "*.db"),
						new ExtensionFilter("All Files", "*.*"));
				fileChooser.setInitialDirectory(new File(path));
				File selectedFile = fileChooser.showOpenDialog(Display.getpStage());
				DataModel.instance().setInputFile(selectedFile);
				DataModel.instance().setBtnRunText("Import");

				if (DataModel.instance().getInputFile() != null) {
					path = DataModel.instance().getInputFile().getAbsolutePath();
					Connection c = FileIO.connectJDBC(path);
					ExecutorService service = Executors.newCachedThreadPool();
					service.submit(new Runnable() {
						@Override
						public void run() {
							System.out.println("Btn: " + DataModel.instance().getBtnRunText());
							DataModel.instance().setBtnRunText("Working..");
							System.out.println("Btn: " + DataModel.instance().getBtnRunText());

							List<Hand> newHands = FileIO.createhands(c);
							for (Hand h : newHands) {
								DataModel.instance().addHand(h);
							}

							DataModel.instance().setBtnDisable(false);
							DataModel.instance().setBtnRunText("Imported!");
							System.out.println("Btn: " + DataModel.instance().getBtnRunText());

						}

					});
				}
			}

		});
	}
}
