package ui;

import core.Quiz;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static ui.TestFxHelper.waitForFxEvents;

public class RegisterTest extends ApplicationTest {
  // Sample input for test
  private final String invalidUsername = "a";
  private final String invalidPassword = "b";
  // Sample input for test
  private final String validUsername = "valid-username";
  private final String validPassword = "valid-password";
  private final String notMatchingPassword = "not-matching";
  private Scene scene;
  private RegisterController registerController;
  private RemoteCognitionAccess remoteCognitionAccess;

  private TestFxHelper helper = new TestFxHelper();

  @BeforeEach
  void setUp() {
    try {
      // Add a user, such that storage is not empty. Empty storage is tested in core
      // module.
      remoteCognitionAccess.create(new User("placeholder", "placeholder"));
    } catch (IOException | InterruptedException e) {
      fail();
    }
  }

  @AfterEach
  void tearDown() {
    TestFxHelper.clearTestStorage();
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = getLoader("Register");

    // The few places in UI code that allegedly do not test the constructor are false,
    // in the way that the constructor is tested here and fails if it cannot be created successfully.
    this.remoteCognitionAccess = new RemoteCognitionAccess(AppTest.TEST_PORT);
    this.registerController = new RegisterController(remoteCognitionAccess);

    loader.setController(registerController);

    scene = new Scene(loader.load());
    stage.setScene(scene);
    stage.show();
  }

  private FXMLLoader getLoader(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("views/" + fxml + ".fxml"));
    return loader;
  }

  @Test
  @DisplayName("Controller is defined.")
  void controllerIsDefined() {
    Assertions.assertNotNull(registerController);
  }

  @Test
  @DisplayName("Storage is defined.")
  void storageIsDefined() {
    Assertions.assertNotNull(remoteCognitionAccess);
  }

  @Test
  @DisplayName("Can go to login view.")
  void canGoToLoginView() {
    // Check that the Register view has loaded correctly
    waitForFxEvents();
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Register"));

    // Click to switch view
    waitForFxEvents();
    clickOn("#switchToLoginButton");

    // Check that the Login view was loaded correctly
    waitForFxEvents();
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Login"));
  }

  @Test
  @DisplayName("Valid register passes.")
  void validRegisterPasses() {
    // Correct input yield success message
    waitForFxEvents();
    verifyInputData(validUsername, validPassword, validPassword, false);

    // Read locally stored users to find the inputted data
    List<User> users = new ArrayList<>();
    try {
      users = remoteCognitionAccess.readUsers();
    } catch (IOException | InterruptedException e) {
      fail();
    }

    // Check if user actually was stored
    boolean userWasStored = false;

    for (User user : users) {
      userWasStored = user.getUsername().equals(validUsername) && user.getPassword().equals(validPassword);

      if (userWasStored) {
        break;
      }
    }

    waitForFxEvents();
    Assertions.assertTrue(userWasStored);
  }

  @Test
  @DisplayName("Default quizzes are added")
  void defaultQuizzes() {
    // Correct input yield success message
    waitForFxEvents();
    verifyInputData(validUsername, validPassword, validPassword, false);

    // Read locally stored users to find the inputted data
    List<User> users = new ArrayList<>();
    try {
      users = remoteCognitionAccess.readUsers();
    } catch (IOException | InterruptedException e) {
      fail();
    }

    User user = users.stream()
            .filter(u -> u.getUsername().equals(validUsername))
            .findFirst()
            .orElse(null);

    Assertions.assertNotNull(user);


    Quiz correctQuiz = RegisterController.createDefaultQuiz();

    // Assert this quiz has been added to the newly registered user
    Assertions.assertEquals(user.getQuizzes().get(0).getName(), correctQuiz.getName());
  }

  @Test
  @DisplayName("All input fields are required.")
  void allInputFieldsAreRequired() {
    waitForFxEvents();
    verifyInputData("", "", "", true);
  }

  @Test
  @DisplayName("Duplicate user gives error message.")
  void duplicateUserGivesErrorMessage() {
    // Create a local user
    verifyInputData(validUsername, validPassword, validPassword, false);

    // Clear input before entering the same data again
    waitForFxEvents();
    helper.clearInputField("#usernameInput");
    waitForFxEvents();
    helper.clearInputField("#passwordInput");
    waitForFxEvents();
    helper.clearInputField("#passwordRepeatInput");

    // Create a local user
    verifyInputData(validUsername, validPassword, validPassword, true);
  }

  @Test
  @DisplayName("Invalid username gives error message.")
  void invalidUsernameGivesErrorMessage() {
    // Invalid username
    waitForFxEvents();
    verifyInputData(invalidUsername, validPassword, validPassword, true);
  }

  @Test
  @DisplayName("Invalid password gives error message.")
  void invalidPasswordGivesErrorMessage() {
    // Invalid password
    waitForFxEvents();
    verifyInputData(validUsername, invalidPassword, invalidPassword, true);
  }

  @Test
  @DisplayName("Non-matching password gives error message.")
  void nonMatchingPasswordGivesErrorMessage() {
    // Passwords do not match
    waitForFxEvents();
    verifyInputData(validUsername, validPassword, notMatchingPassword, true);
  }

  /**
   * A helper method for verifying input data.
   *
   * @param username       is the provided username in the input form
   * @param password       is the provided password in the input form
   * @param repeatPassword is the provided repeated password in the input form
   * @param isErrorMessage describes if the feedback message should be an error or
   *                       success
   */
  private void verifyInputData(String username, String password, String repeatPassword, boolean isErrorMessage) {
    // Input data into UI
    waitForFxEvents();
    clickOn(helper.findTextField(node -> true, "#usernameInput", 0)).write(username);
    waitForFxEvents();
    clickOn(helper.findTextField(node -> true, "#passwordInput", 0)).write(password);
    waitForFxEvents();
    clickOn(helper.findTextField(node -> true, "#passwordRepeatInput", 0)).write(repeatPassword);
    waitForFxEvents();
    clickOn("#registerButton");

    String feedback = isErrorMessage ? registerController.getFeedbackErrorMessage()
            : registerController.getFeedbackSuccessMessage();

    // Validate that user got correct feedback in UI
    waitForFxEvents();
    FxAssert.verifyThat("#feedback", LabeledMatchers.hasText(feedback));
  }
}
