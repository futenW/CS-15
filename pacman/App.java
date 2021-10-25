package pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// runs entire application
public class App extends Application {
    // instantiates top-level object, sets up scene, shows stage
    @Override
    public void start(Stage stage) {
        PaneOrganizer organizer = new PaneOrganizer();
        Scene scene = new Scene(organizer.getRoot(), Const.BOARD_WIDTH, Const.BOARD_WIDTH);
        stage.setScene(scene);
        stage.show();
    }

    // here is the mainline! No need to change this.
    public static void main(String[] argv) {
        launch(argv); // launch is a method inherited from Application
    }
}