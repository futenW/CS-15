package cartoon;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


// runs all animations, key-handling, and event-handling
public class Cartoon {
    Pane _heartPane1; // get it? heart.. pane... >.<
    Pane _heartPane2;
    Pane _heartPane3;
    Pane _heartPane4;

    BorderPane _topPane; // panes in each entry of root BorderPane
    BorderPane _bottomPane;
    Pane _leftPane;
    Pane _rightPane;

    BorderPane _imagePane;
    BorderPane _rootNode; // need to associate root with Cartoon to change background colors at certain points

    // top-level logic class that contains event handlers, other "game" logic, and instances of composite shapes
    public Cartoon(Pane heartPane1, Pane heartPane2, Pane heartPane3, Pane heartPane4, BorderPane imagePane,
                   BorderPane topPane, BorderPane bottomPane, Pane leftPane, Pane rightPane, BorderPane root) {
        _heartPane1 = heartPane1;
        _heartPane2 = heartPane2;
        _heartPane3 = heartPane3;
        _heartPane4 = heartPane4;

        _topPane = topPane;
        _bottomPane = bottomPane;
        _leftPane = leftPane;
        _rightPane = rightPane;

        _imagePane = imagePane;
        _rootNode = root;

        // get image of Blueno hugging teddy bear
        ImageView blueno = this.hugBlueno();
        blueno.setFocusTraversable(false);
        _imagePane.setCenter(blueno);

        // create hearts
        new Heart(_heartPane1, Constants.CENTER_X-45,Constants.HIGHER_Y,12); // heart 1
        new Heart(_heartPane2, Constants.CENTER_X, Constants.HIGHER_Y, 6); // heart 2
        new Heart(_heartPane3, Constants.CENTER_X+50,Constants.CENTER_Y,9); // heart 3
        new Heart(_heartPane4, Constants.CENTER_X+100, Constants.CENTER_Y, 14); // heart 4

        // set up key-handling
        _heartPane1.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
        _heartPane1.requestFocus();
        _heartPane1.setFocusTraversable(true);
        _heartPane2.setFocusTraversable(false);
        _heartPane3.setFocusTraversable(false);
        _heartPane4.setFocusTraversable(false);
        _imagePane.setFocusTraversable(false);
        _topPane.setFocusTraversable(false);
        _bottomPane.setFocusTraversable(false);
        _leftPane.setFocusTraversable(false);
        _rightPane.setFocusTraversable(false);

        this.setupTimeline();
    }

    // returns image of Blueno hugging teddy bear
    private ImageView hugBlueno() {
        ImageView blueno = new ImageView(new Image("./cartoon/blueno.jpg"));
        blueno.setFitWidth(Constants.CENTER_X);
        blueno.setPreserveRatio(true);
        return blueno;
    }


    // set up heart animations
    private void setupTimeline() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.008), new heartsHandler()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    // animates hearts to move up and down, only showing when moving upwards
    private class heartsHandler implements EventHandler<ActionEvent> {
        boolean goUp = true; // keeps track of hearts 1 & 3 movement directions
        boolean goDown = true; // keeps track of hearts 2 & 4 movement directions
        boolean firstTime = true;

        // creates each keyframe of hearts moving
        @Override
        public void handle(ActionEvent event) {
            if (firstTime) { // hides animations for the first few (milli)seconds
                if (!goDown) {
                    firstTime = false;
                }
                _heartPane3.setVisible(false);
                _heartPane4.setVisible(false);
            }
            goUp = this.animateHeart(_heartPane1, _heartPane3, goUp, 200, 60); // hearts 1 & 3 move in opposite directions
            goDown = this.animateHeart(_heartPane2, _heartPane4, goDown, 210, 50); // hearts 2 & 4 move in opposite directions
        }

        // makes two hearts move up and down in opposite directions, hides the heart when going down
        private boolean animateHeart(Pane heartPane, Pane heartPane2, boolean moveDirection, int minHeight, int maxHeight) {
            if (moveDirection) { // if first heart goes up, second heart goes down
                heartPane.setLayoutY(heartPane.getLayoutY() - 1);
                heartPane2.setLayoutY(heartPane2.getLayoutY() + 1);
                if (heartPane.getLayoutY() < maxHeight) { // if first heart has reached the top, reverse both hearts' directions
                    moveDirection = !moveDirection;
                    heartPane.setVisible(false);
                    heartPane2.setVisible(true);
                }
            } else { // if first heart goes down, second heart goes up
                heartPane.setLayoutY(heartPane.getLayoutY() + 1);
                heartPane2.setLayoutY(heartPane2.getLayoutY() - 1);
                if (heartPane.getLayoutY() > minHeight) { // if first heart has reached the bottom, reverse both hearts' directions
                    moveDirection = !moveDirection;
                    heartPane.setVisible(true);
                    heartPane2.setVisible(false);
                }
            }
            return moveDirection; // return which direction to move for next key frame
                                    // (since local variables get discarded after method termination)
        }
    }

    // Detect user key input and initiate appropriate response
    private class KeyHandler implements EventHandler<KeyEvent> {
        int r = Constants.HEART_RED; int g = Constants.HEART_GREEN; int b = Constants.HEART_BLUE; // hearts' initial color
        int R = Constants.BACK_RED; int G = Constants.BACK_GREEN; int B = Constants.BACK_BLUE; // background's initial color

        // handle key inputs
        @Override
        public void handle(KeyEvent e) {
            KeyCode keyPressed = e.getCode();
            if (keyPressed == KeyCode.RIGHT) { // increment heart RGB
                for (int i = 0; i < Constants.STANDARD_ITERATIONS*2; i++) { // makes the color change faster
                    this.nextHeartColor();
                }
                ((Shape)_heartPane1.getChildren().get(0)).setFill(Color.rgb(r, g, b));
                ((Shape)_heartPane2.getChildren().get(0)).setFill(Color.rgb(r, g, b));
                ((Shape)_heartPane3.getChildren().get(0)).setFill(Color.rgb(r, g, b));
                ((Shape)_heartPane4.getChildren().get(0)).setFill(Color.rgb(r, g, b));
            } else if (keyPressed == KeyCode.LEFT){ // decrement heart RGB
                for (int i = 0; i < Constants.STANDARD_ITERATIONS*2; i++) { // makes the color change faster
                    this.lastHeartColor();
                }
                ((Shape)_heartPane1.getChildren().get(0)).setFill(Color.rgb(r, g, b));
                ((Shape)_heartPane2.getChildren().get(0)).setFill(Color.rgb(r, g, b));
                ((Shape)_heartPane3.getChildren().get(0)).setFill(Color.rgb(r, g, b));
                ((Shape)_heartPane4.getChildren().get(0)).setFill(Color.rgb(r, g, b));
            } else if (keyPressed == KeyCode.DOWN) { // increment background RGB
                for (int i = 0; i < Constants.STANDARD_ITERATIONS; i++) { // makes the color change faster
                    this.nextBackgroundColor();
                }
                String backgroundColor = "-fx-background-color: rgb(" + R + "," + G + "," + B + ");";
                _rootNode.setStyle(backgroundColor);
            } else if (keyPressed == KeyCode.UP) { // decrement background RGB
                for (int i = 0; i < Constants.STANDARD_ITERATIONS; i++) { // makes the color change faster
                    this.lastBackgroundColor();
                }
                String backgroundColor = "-fx-background-color: rgb(" + R + "," + G + "," + B + ");";
                _rootNode.setStyle(backgroundColor);
            }
            else if (keyPressed == KeyCode.F) {
                this.haveASeizure(); // self explainable ;)

                // create quit button
                Button quit = new Button("make it stop");
                Button fake = new Button(); // dummy button to mirror quit button so Blueno doesn't shift upwards
                quit.setFocusTraversable(false);
                fake.setFocusTraversable(false);
                _bottomPane.setPrefHeight(Constants.CENTER_Y/2);
                _topPane.setPrefHeight(Constants.CENTER_Y/2); // dummy pane to mirror _bottomPane so Blueno stays in place
                _bottomPane.setTop(quit);
                _topPane.setCenter(fake);
                _bottomPane.setAlignment(quit, Pos.CENTER);
                fake.setVisible(false); // hide dummy button
                quit.setVisible(false); // hide quit button initially
                this.showQuitButton(quit); // reveal after delay
                quit.setOnAction(new exitHandler()); // exit program
            }

            e.consume();
        }

        // :)
        private void haveASeizure() {
            Timeline seizureTime = new Timeline(new KeyFrame(Duration.seconds(.02), new SeizureHandler()));
            seizureTime.setCycleCount(Animation.INDEFINITE);
            seizureTime.play();
        }

        // initiate spastic color changes
        private class SeizureHandler implements EventHandler<ActionEvent> {
            boolean invertCases = true; // keeps track of which set of text to show

            // changes background to random colors and gives Blueno a seizure
            @Override
            public void handle(ActionEvent event) {
                R = (int)(Math.random()*Constants.RGB_MAX+1);
                G = (int)(Math.random()*Constants.RGB_MAX+1);
                B = (int)(Math.random()*Constants.RGB_MAX+1);
                String backgroundColor = "-fx-background-color: rgb(" + R + "," + G + "," + B + ");";
                _rootNode.setStyle(backgroundColor);

                // Create labels
                Label leftWords = new Label();
                Label rightWords = new Label();
                if (invertCases) {
                    leftWords.setText("dEaR");
                    rightWords.setText("bLuEnO");
                    invertCases = !invertCases;
                } else if (!invertCases) {
                    leftWords.setText("DeAr");
                    rightWords.setText("BlUeNo");
                    invertCases = !invertCases;
                }
                _leftPane.getChildren().clear(); // get rid of text previously on screen
                _rightPane.getChildren().clear();

                _leftPane.getChildren().add(leftWords); // add new set of text
                _rightPane.getChildren().add(rightWords);

                leftWords.setStyle("-fx-font-size: 43");
                rightWords.setStyle("-fx-font-size: 55");

                leftWords.setLayoutX(leftWords.getLayoutX() + Constants.CENTER_X/5);
                rightWords.setLayoutX(rightWords.getLayoutX() - Constants.CENTER_X/5);
                leftWords.setLayoutX(leftWords.getLayoutX() + (int)(Math.random()*Constants.CENTER_X/4)); // make the words
                rightWords.setLayoutX(rightWords.getLayoutX() - (int)(Math.random()*Constants.CENTER_X/4)); // twitch left & right
            }
        }

        // increment RGB of hearts
        private void nextHeartColor() {
            if (r == Constants.RGB_MAX && (g >= Constants.THRESHOLD_1 && g < Constants.RGB_MAX) && b == Constants.THRESHOLD_2) {
                g++;
            } else if ((r <= Constants.RGB_MAX && r > Constants.THRESHOLD_2) && g == Constants.RGB_MAX && b == Constants.THRESHOLD_2) {
                r--;
            } else if (r == Constants.THRESHOLD_2 && g == Constants.RGB_MAX && (b >= Constants.THRESHOLD_2 && b < Constants.RGB_MAX)) {
                b++;
            } else if (r == Constants.THRESHOLD_2 && (g <= Constants.RGB_MAX && g > Constants.THRESHOLD_2) && b == Constants.RGB_MAX) {
                g--;
            } else if ((r >= Constants.THRESHOLD_2 && r < Constants.RGB_MAX) && g == Constants.THRESHOLD_2 && b == Constants.RGB_MAX) {
                r++;
            } else if (r == Constants.RGB_MAX && g == Constants.THRESHOLD_2 && (b <= Constants.RGB_MAX && b > Constants.THRESHOLD_2)) {
                b--;
            } else {
                g++;
            }
        }

        // decrement RGB of hearts
        private void lastHeartColor() {
            if (r == Constants.RGB_MAX && (g <= Constants.THRESHOLD_1 && g > Constants.THRESHOLD_2) && b == Constants.THRESHOLD_2) {
                g--;
            } else if (r == Constants.RGB_MAX && g == Constants.THRESHOLD_2 && (b >= Constants.THRESHOLD_2 && b < Constants.RGB_MAX)) {
                b++;
            } else if ((r <=Constants.RGB_MAX && r > Constants.THRESHOLD_2) && g == Constants.THRESHOLD_2 && b == Constants.RGB_MAX) {
                r--;
            } else if (r == Constants.THRESHOLD_2 && (g >= Constants.THRESHOLD_2 && g < Constants.RGB_MAX) && b == Constants.RGB_MAX) {
                g++;
            } else if (r == Constants.THRESHOLD_2 && g == Constants.RGB_MAX && (b <= Constants.RGB_MAX && b > Constants.THRESHOLD_2)) {
                b--;
            } else if ((r >= Constants.THRESHOLD_2 && r < Constants.RGB_MAX) && g == Constants.RGB_MAX && b == Constants.THRESHOLD_2) {
                r++;
            } else {
                g--;
            }
        }

        // increment RGB of background
        private void nextBackgroundColor() {
            if (R == Constants.THRESHOLD_3 && (G <= Constants.THRESHOLD_4 && G > Constants.THRESHOLD_3) && B == Constants.THRESHOLD_5) {
                G--;
            } else if ((R >= Constants.THRESHOLD_3 && R < Constants.THRESHOLD_5) && G == Constants.THRESHOLD_3 && B == Constants.THRESHOLD_5) {
                R++;
            } else if (R == Constants.THRESHOLD_5 && G == Constants.THRESHOLD_3 && (B <= Constants.THRESHOLD_5 && B > Constants.THRESHOLD_3)) {
                B--;
            } else if (R == Constants.THRESHOLD_5 && (G >= Constants.THRESHOLD_3 && G < Constants.THRESHOLD_5) && B == Constants.THRESHOLD_3) {
                G++;
            } else if ((R <= Constants.THRESHOLD_5 && R > Constants.THRESHOLD_3) && G == Constants.THRESHOLD_5 && B == Constants.THRESHOLD_3) {
                R--;
            } else if (R == Constants.THRESHOLD_3 && G == Constants.THRESHOLD_5 && (B >= Constants.THRESHOLD_3 && B < Constants.THRESHOLD_5)) {
                B++;
            } else {
                G--;
            }
        }

        // decrement RGB of background
        private void lastBackgroundColor() {
            if (R == Constants.THRESHOLD_3 && (G <= Constants.THRESHOLD_4 && G < Constants.THRESHOLD_5) && B == Constants.THRESHOLD_5) {
                G++;
            } else if (R == Constants.THRESHOLD_3 && G == Constants.THRESHOLD_5 && (B <= Constants.THRESHOLD_5 && B > Constants.THRESHOLD_3)) {
                B--;
            } else if ((R >= Constants.THRESHOLD_3 && R < Constants.THRESHOLD_5) && G == Constants.THRESHOLD_5 && B == Constants.THRESHOLD_3) {
                R++;
            } else if (R == Constants.THRESHOLD_5 && (G <= Constants.THRESHOLD_5 && G > Constants.THRESHOLD_3) && B == Constants.THRESHOLD_3) {
                G--;
            } else if (R == Constants.THRESHOLD_5 && G == Constants.THRESHOLD_3 && (B >= Constants.THRESHOLD_3 && B < Constants.THRESHOLD_5)) {
                B++;
            } else if ((R <= Constants.THRESHOLD_5 && R > Constants.THRESHOLD_3 && G == Constants.THRESHOLD_3 && B == Constants.THRESHOLD_5)) {
                R--;
            } else {
                G++;
            }
        }

        // set up animation to reveal quit button
        private void showQuitButton(Button btn) {
            Timeline showButton = new Timeline(new KeyFrame(Duration.seconds(3), new buttonRevealer(btn)));
            showButton.setCycleCount(1);
            showButton.play();
        }

        // reveal quit button
        private class buttonRevealer implements EventHandler<ActionEvent> {
            Button btn;
            // set up association of button
            public buttonRevealer(Button button) {
                btn = button;
            }
            // make button visible
            @Override
            public void handle(ActionEvent event) {
                btn.setVisible(true);
            }
        }

        // event handler to exit program
        private class exitHandler implements EventHandler<ActionEvent> {
            @Override
            // literally exit program
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        }
    }
}