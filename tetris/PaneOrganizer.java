package tetris;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

// organizes panes for entire game
public class PaneOrganizer {
    private Pane _root; // root pane

    // organizes panes
    public PaneOrganizer() {
        _root = new Pane();
        _root.setStyle("-fx-background-color: black;");

        // set up game and create quit button
        Game game = new Game(_root);
        this.setupQuit();
    }

    // return root
    public Pane getRoot() {
        return _root;
    }

    // creates quit button, adds it to a Pane, sets up quit event
    private void setupQuit() {
        Button quit = new Button("Quit");
        quit.setFocusTraversable(false);
        _root.getChildren().add(quit);
        quit.setLayoutX(Constants.BOARD_WIDTH / 2 - Constants.ROW_NUM);
        quit.setLayoutY(Constants.BOARD_HEIGHT - Constants.SQ_WIDTH);
        quit.setOnAction(new ExitHandler());
    }

    // event handler to exit program
    private class ExitHandler implements EventHandler<ActionEvent> {
        // calls System.exit
        @Override
        public void handle(ActionEvent event) {
            System.exit(0);
        }
    }
}
