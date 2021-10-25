package tetris;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// runs entire application
public class App extends Application {
    // instantiates top-level object, sets up scene, shows stage
    @Override
    public void start(Stage stage) {
        PaneOrganizer organizer = new PaneOrganizer();
        Scene scene = new Scene(organizer.getRoot(), Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    // Here is the mainline! No need to change this.
    public static void main(String[] argv) {
        // launch is a method inherited from Application
        launch(argv);
    }
}
