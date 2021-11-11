package ui;

import core.Flashcard;
import core.Quiz;
import core.User;
import core.tools.Tools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


/**
 * QuizController handles the presentation logic of creating and updating a quiz.
 */
public class QuizController extends LoggedInController {
  private static final String feedbackSuccessMessage = "Successfully created quiz.";

  @FXML
  private ScrollPane flashcardPane;

  @FXML
  private Button storeQuizButton;

  @FXML
  private Text heading;

  @FXML
  private Label feedback;

  @FXML
  private TextField name;

  @FXML
  private TextField description;

  private VBox flashcardPaneContainer = new VBox();
  private String feedbackErrorMessage = "";
  private Quiz quizBeingUpdated = null;

  public QuizController(User user, RemoteCognitionAccess remoteCognitionAccess) {
    super(user, remoteCognitionAccess);
    flashcardPaneContainer.setId("flashcardPaneContainer");
  }

  public void setQuizBeingUpdated(Quiz quizBeingUpdated) {
    this.quizBeingUpdated = quizBeingUpdated;
  }

  /**
   * Renders the initial view.
   */
  @FXML
  private void initialize() {
    if (quizBeingUpdated != null) {
      // populate ui according to quiz being updated
      name.setText(quizBeingUpdated.getName());
      description.setText(quizBeingUpdated.getDescription());
      storeQuizButton.setText("Update quiz");
      heading.setText("Update " + quizBeingUpdated.getName());


      for (Flashcard flashcard : quizBeingUpdated.getFlashcards()) {

        createFlashcardNode(flashcard);
      }
    } else {
      // new quiz -> start with one empty flashcard and empty name and description
      createFlashcardNode(null);
    }
  }

  /**
   * Removes a Flashcard from the ScrollPane in the UI.
   */
  private void removeFlashcardNode(int nodeId) {
    flashcardPaneContainer.getChildren().remove(nodeId);

    // Update view
    updateFlashcardNodes();
  }

  public String getFeedbackErrorMessage() {
    return feedbackErrorMessage;
  }

  public String getFeedbackSuccessMessage() {
    return feedbackSuccessMessage;
  }

  /**
   * Updates view with new index values for relevant components.
   */
  private void updateFlashcardNodes() {

    feedbackErrorMessage = "An error occurred when updating the flashcard inputs.";

    for (int i = 0; i < flashcardPaneContainer.getChildren().size(); i++) {
      // Find "remove flashcard" Button and "flashcard number" Text in nested UI
      // component
      VBox parent = (VBox) flashcardPaneContainer.getChildren().get(i);
      HBox upperChildContainer = (HBox) parent.getChildren().stream()
              .filter(node -> node.getId() != null && node.getId().equals("upper-child-container"))
              .findFirst()
              .orElse(null);

      if (upperChildContainer == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText(feedbackErrorMessage);

        return;
      }

      Button removeFlashcardButton = (Button) upperChildContainer.getChildren().stream()
              .filter(node -> node.getId() != null
                      && node.getId().equals("remove-flashcard-button"))
              .findFirst()
              .orElse(null);

      if (removeFlashcardButton == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText(feedbackErrorMessage);

        return;
      }

      Text flashcardNumberText = (Text) upperChildContainer.getChildren().stream()
              .filter(node -> node.getId() != null
                      && node.getId().equals("flashcard-number-text"))
              .findFirst()
              .orElse(null);

      if (flashcardNumberText == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText(feedbackErrorMessage);

        return;
      }

      // This is necessary for updating the onAction event handler
      int index = i;

      // Update state in relevant components
      removeFlashcardButton.setOnAction((event) -> removeFlashcardNode(index));
      flashcardNumberText.setText(String.valueOf(index + 1));
    }
  }

  /**
   * Adds a Flashcard to the quiz.
   *
   * @param actionEvent is the ActionEvent from the UI
   */
  @FXML
  private void addFlashcardNode(ActionEvent actionEvent) {
    createFlashcardNode(null);
    feedback.setTextFill(Color.GREEN);
    feedback.setText("Added flashcard.");
  }

  /**
   * Dynamically creates a Flashcard node to be used in the view.
   */
  private void createFlashcardNode(Flashcard flashcard) {
    int nodeCount = flashcardPaneContainer.getChildren().size();

    // Populate upper child container
    Text flashcardNumberText = new Text();
    flashcardNumberText.setId("flashcard-number-text");
    flashcardNumberText.setText(String.valueOf(nodeCount + 1));
    flashcardNumberText.setFont(Font.font("Avenir Book", 20));
    ;

    // Set "remove flashcard" button properties
    Button removeFlashcardButton = new Button();
    removeFlashcardButton.setText("X");
    removeFlashcardButton.setId("remove-flashcard-button");
    removeFlashcardButton.setOnAction((event) -> removeFlashcardNode(nodeCount));
    removeFlashcardButton.setFont(Font.font("Avenir Book", 16));
    removeFlashcardButton.setStyle("-fx-background-color: #EE4040; -fx-background-radius: 10;");
    removeFlashcardButton.setTextFill(Color.WHITE);


    HBox upperChildContainer = new HBox();
    // Add child elements to upper child container
    upperChildContainer.setId("upper-child-container");
    upperChildContainer.getChildren().addAll(flashcardNumberText, removeFlashcardButton);
    upperChildContainer.setSpacing(690);

    // Populate front container
    VBox frontContainer = new VBox();
    frontContainer.setId("front-container");
    Label frontLabel = new Label();
    frontLabel.setText("Front");
    frontLabel.setFont(Font.font("Avenir Book", 20));
    TextField frontInput = new TextField();
    frontInput.setId("front-input");
    frontInput.setPrefWidth(340);
    frontInput.setMaxWidth(340);
    frontInput.setMaxHeight(Double.MAX_VALUE);
    frontInput.setFont(Font.font("Avenir Book", 18));
    if (flashcard != null) {
      frontInput.setText(flashcard.getFront());
    }
    frontContainer.getChildren().addAll(frontInput, frontLabel);

    // Populate answer container
    VBox answerContainer = new VBox();
    answerContainer.setId("answer-container");
    Label answerLabel = new Label();
    answerLabel.setText("Answer");
    answerLabel.setFont(Font.font("Avenir Book", 20));
    TextField answerInput = new TextField();
    answerInput.setId("answer-input");
    answerInput.setPrefWidth(340);
    answerInput.setMaxWidth(340);
    answerInput.setMaxHeight(Double.MAX_VALUE);
    answerInput.setFont(Font.font("Avenir Book", 18));
    if (flashcard != null) {
      answerInput.setText(flashcard.getAnswer());
    }
    answerContainer.getChildren().addAll(answerInput, answerLabel);

    HBox lowerChildContainer = new HBox();
    // Add child elements to lower child container
    lowerChildContainer.setId("lower-child-container");
    lowerChildContainer.getChildren().addAll(frontContainer, answerContainer);
    lowerChildContainer.setSpacing(50);


    Line division = new Line();
    division.setStrokeWidth(2);
    division.setStartX(0);
    division.setEndX(740);
    division.setStroke(Color.GRAY);

    Line spacing = new Line();
    spacing.setStrokeWidth(20);
    spacing.setStroke(Color.TRANSPARENT);
    spacing.setStartX(0);
    spacing.setEndX(740);

    Line spacing2 = new Line();
    spacing2.setStroke(Color.TRANSPARENT);
    spacing.setStartX(0);
    spacing.setEndX(740);
    spacing.setStrokeWidth(30);


    VBox parentContainer = new VBox();
    parentContainer.setId("parent-container");
    // Add child containers to parent container
    parentContainer.getChildren()
            .addAll(spacing, upperChildContainer, spacing2, lowerChildContainer, division);

    // Add dynamically generated content to view
    flashcardPaneContainer.getChildren().add(parentContainer);
    flashcardPane.setContent(flashcardPaneContainer);
  }

  /**
   * Stores the quiz locally, either by updating an existing one
   * or create a new quiz.
   *
   * @param actionEvent is the ActionEvent on button click.
   */
  @FXML
  private void handleStoreQuiz(ActionEvent actionEvent) {
    String quizName = name.getText();
    String quizDescription = description.getText();

    if (!Quiz.isValidName(quizName)) {
      feedback.setTextFill(Color.RED);
      feedbackErrorMessage = "The name of a quiz cannot be empty.";
      setFeedbackText(feedbackErrorMessage);
      return;
    }

    if (!Quiz.isValidDescription(quizDescription)) {
      feedback.setTextFill(Color.RED);
      feedbackErrorMessage = "The description of a quiz cannot be empty.";
      setFeedbackText(feedbackErrorMessage);
      return;
    }

    List<Flashcard> flashcards = getFlashcards();

    // Custom feedback response has already been provided to the user in the
    // getFlashcards() method
    if (flashcards == null) {
      return;
    }

    for (Flashcard flashcard : flashcards) {
      if (!Flashcard.isValidFront(flashcard.getFront())
              || !Flashcard.isValidAnswer(flashcard.getAnswer())) {
        feedback.setTextFill(Color.RED);
        feedbackErrorMessage = "Both front and answer in each flashcards cannot be empty.";
        setFeedbackText(feedbackErrorMessage);
        return;
      }
    }

    // If there is a quiz, update the existing quiz
    if (quizBeingUpdated != null) {
      quizBeingUpdated.setName(quizName);
      quizBeingUpdated.setDescription(quizDescription);
      quizBeingUpdated.setFlashcards(flashcards);
      getUser().updateQuiz(quizBeingUpdated);
    } else {
      // If quiz == null (does not exist), create a new one and add it
      Quiz newQuiz = new Quiz(Tools.createUuid(), quizName, quizDescription);
      newQuiz.addFlashcards(flashcards);
      getUser().addQuiz(newQuiz);
    }

    try {
      getCognitionAccess().update(getUser());
      feedback.setTextFill(Color.GREEN);
      setFeedbackText(getFeedbackSuccessMessage());

    } catch (IOException | InterruptedException e) {
      feedback.setTextFill(Color.RED);
      feedbackErrorMessage = "An error occurred when trying to create the quiz.";
      setFeedbackText(feedbackErrorMessage);
    }
  }

  private List<Flashcard> getFlashcards() {
    List<Flashcard> flashcards = new ArrayList<>();

    for (int i = 0; i < flashcardPaneContainer.getChildren().size(); i++) {
      VBox parent = (VBox) flashcardPaneContainer.getChildren().get(i);
      HBox lowerChildContainer = (HBox) parent.getChildren().stream()
              .filter(node -> node.getId() != null && node.getId().equals("lower-child-container"))
              .findFirst()
              .orElse(null);

      if (lowerChildContainer == null) {

        feedback.setTextFill(Color.RED);
        setFeedbackText("An error occurred when parsing the flashcards.");

        return null;
      }

      VBox frontContainer = (VBox) lowerChildContainer.getChildren().stream()
              .filter(node -> node.getId() != null && node.getId().equals("front-container"))
              .findFirst()
              .orElse(null);

      if (frontContainer == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText("An error occurred when parsing the flashcards.");
        return null;
      }

      TextField frontInput = (TextField) frontContainer.getChildren().stream()
              .filter(node -> node.getId() != null && node.getId().equals("front-input"))
              .findFirst()
              .orElse(null);

      if (frontInput == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText("An error occurred when parsing the flashcards.");

        return null;
      }

      VBox answerContainer = (VBox) lowerChildContainer.getChildren().stream()
              .filter(node -> node.getId() != null && node.getId().equals("answer-container"))
              .findFirst()
              .orElse(null);

      if (answerContainer == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText("An error occurred when parsing the flashcards.");
        return null;
      }

      TextField answerInput = (TextField) answerContainer.getChildren().stream()
              .filter(node -> node.getId() != null && node.getId().equals("answer-input"))
              .findFirst()
              .orElse(null);

      if (answerInput == null) {
        feedback.setTextFill(Color.RED);
        setFeedbackText("An error occurred when parsing the flashcards.");
        return null;
      }

      // The actual string content of the front input
      String front = frontInput.getText();
      String answer = answerInput.getText();

      if (!Flashcard.isValidFront(front)) {
        feedback.setTextFill(Color.RED);
        setFeedbackText("The front of the flashcard cannot be empty.");
        return null;
      }

      if (!Flashcard.isValidFront(answer)) {
        feedback.setTextFill(Color.RED);
        setFeedbackText("The answer of the flashcard cannot be empty.");
        return null;
      }

      Flashcard flashcard = new Flashcard(Tools.createUuid(), front, answer);
      flashcards.add(flashcard);
    }

    return flashcards;
  }

  /**
   * Handles how a switch to the same controller should be handled. If currently updating quiz, user
   * is able to switch to create new quiz. Otherwise, it is not permitted.
   *
   * @param event is the given by javafx
   */
  @Override
  protected void handleCreateQuiz(ActionEvent event) {
    if (quizBeingUpdated == null) {
      return;
    }
    QuizController quizController = new QuizController(getUser(), getCognitionAccess());
    changeToView(event, quizController, "Quiz");
  }
}