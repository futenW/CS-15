package cartoon;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

// sets up panes and to creates instance of top-level logic class
public class PaneOrganizer {
    BorderPane _root;

    // sets up root, its panes, and cartoon class
    public PaneOrganizer() {
        // set up panes
        Pane heartPane1 = new Pane();
        Pane heartPane2 = new Pane();
        Pane heartPane3 = new Pane();
        Pane heartPane4 = new Pane();
        BorderPane imagePane = new BorderPane();
        BorderPane topPane = new BorderPane();
        BorderPane bottomPane = new BorderPane();
        Pane leftPane = new Pane();
        Pane rightPane = new Pane();

        // set up root
        _root = new BorderPane();
        _root.setFocusTraversable(false);

        // add panes to root
        _root.setCenter(imagePane);
        _root.getChildren().addAll(heartPane1, heartPane2, heartPane3, heartPane4);
        _root.setLeft(leftPane);
        _root.setRight(rightPane);
        _root.setBottom(bottomPane);
        _root.setTop(topPane);

        // set background color
        String backgroundColor = "-fx-background-color: rgb" + "(" + Constants.BACK_RED + "," +
                Constants.BACK_GREEN + "," + Constants.BACK_BLUE + ");";
        _root.setStyle(backgroundColor); // sets background color

        // set up cartoon and add all panes
        Cartoon cartoon = new Cartoon(heartPane1, heartPane2, heartPane3, heartPane4,
                imagePane, topPane, bottomPane, leftPane, rightPane, _root);
    }

    // returns root
    public BorderPane getRoot() {
        return _root;
    }
}
