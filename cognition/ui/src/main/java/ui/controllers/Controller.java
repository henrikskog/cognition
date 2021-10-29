package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import json.CognitionStorage;

import java.io.IOException;

/**
 * Controller is an abstract class with the common functionality
 * for all presentation layer controllers.
 */
public abstract class Controller {

  @FXML
  private Label feedback;

  private CognitionStorage cognitionStorage;

  public Controller(CognitionStorage cognitionStorage) {
    this.cognitionStorage = cognitionStorage;
  }

  public Controller() {
  }

  public Stage getStage(ActionEvent event) {
    Node node = (Node) event.getSource();
    return (Stage) node.getScene().getWindow();
  }

  /**
   * Gets the loader for an FXML view.
   *
   * @param fxml is the String representation of the FXML filename.
   * @return an FXMLLoader with the location set to the provided FXML file.
   */
  public FXMLLoader getLoader(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    // Using Controller.class, rather than this.getClass(),
    // SpotBugs does not find a compile-time bug.
    loader.setLocation(Controller.class.getResource("../views/" + fxml + ".fxml"));
    return loader;
  }

  /**
   * Switches scene in the stage.
   *
   * @param stage is the stage to show
   * @param root  is the root of the loaded FXML
   * @throws IOException if an error occurs when switching scenes.
   */
  protected void switchScene(Stage stage, Parent root) throws IOException {
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Changes from the current view to a pr
   * ovided view.
   * The method loads the FXML and sets the new controller,
   * then switches stage.
   *
   * @param event      is the ActionEvent on button click.
   * @param controller is the presentation logic Controller.
   * @param fxml       is the String representation of the FXML filename.
   */
  protected void changeToView(ActionEvent event,
                           Controller controller,
                           String fxml) {
    FXMLLoader loader = getLoader(fxml);

    loader.setController(controller);

    Stage stage = getStage(event);

    try {
      switchScene(stage, loader.load());
    } catch (IOException e) {
      feedback.setText("An error occurred when trying to go to " + fxml + ".");
    }
  }

  public CognitionStorage getCognitionStorage() {
    return cognitionStorage;
  }

  public void setCognitionStorage(CognitionStorage cognitionStorage) {
    this.cognitionStorage = cognitionStorage;
  }

  public Label getFeedback() {
    return feedback;
  }

  public void setFeedbackText(String value) {
    feedback.setText(value);
  }
}
