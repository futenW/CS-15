package doodlejump;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

// sets up panes and to creates instance of top-level logic class
public class PaneOrganizer {
    private BorderPane _root;

    // sets up root, its panes, and cartoon class
    public PaneOrganizer() {
        // set up panes
        Pane gamePane = new Pane();
        BorderPane quitPane = new BorderPane();
        quitPane.setPrefHeight(75);

        // set up root
        _root = new BorderPane();
        _root.setFocusTraversable(false);

        // add panes to root
        _root.setCenter(gamePane);
        _root.setBottom(quitPane);

        // set background color and quit button
        _root.setStyle("-fx-background-color: rgb(177,246,255);");
        this.setupQuit(quitPane); // create quit button

        // set up game and add all panes
        Game game = new Game(gamePane);
    }

    // getter method for root
    public BorderPane getRoot() {
        return _root;
    }

    // creates quit button, adds it to a Pane, sets up quit event
    private void setupQuit(BorderPane quitPane) {
        Button quit = new Button("Quit");
        quit.setFocusTraversable(false);
        quitPane.setCenter(quit);
        quit.setOnAction(new PaneOrganizer.ExitHandler());
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