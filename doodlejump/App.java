package doodlejump;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// main class where Game game will start, calls launch, which eventually calls the start method below
public class App extends Application {

    // instantiates top-level object, sets up scene, shows stage
    @Override
    public void start(Stage stage) {
        PaneOrganizer organizer = new PaneOrganizer();
        Scene scene = new Scene(organizer.getRoot(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    // Here is the mainline! No need to change this.
    public static void main(String[] argv) {
        launch(argv); // launch is a static method inherited from Application
    }
}