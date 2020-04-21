package ClientUI;

import Client.ClientController;
import ObserverPatterns.SignUpResultListener;
import Shared.Packet;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable, SignUpResultListener
{
    @FXML
    StackPane parentContainerSignUp;
    @FXML
    AnchorPane anchorPane;
    @FXML
    private TextField txtF_FirstName, txtF_LastName, txtF_Username;
    @FXML
    private PasswordField txtF_Password, txtF_ConfirmPassword;
    @FXML
    private Button btn_SignIn, btn_SignUp;
    @FXML
    private Label firstNameError, lastNameError, usernameError, passwordError, confirmPasswordError;

    private ClientController controller;

    private SignInController signInController;

    @FXML
    public void OnEnterKeyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            String firstName = txtF_FirstName.getText().trim();
            String lastName = txtF_LastName.getText().trim();
            String username = txtF_Username.getText().trim();
            String password = txtF_Password.getText().trim();
            String confirmPassword = txtF_ConfirmPassword.getText().trim();

            if (! checkPasswords(password, confirmPassword) && checkField(firstName, lastName, username, password, confirmPassword))
            {
                // notify user that text fields need to be fixed

                System.out.println("Fields were not entered correctly");


                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {

                    }
                });
            }
            else
            {
                System.out.println("Registering new user");
                registerNewUser(firstName, lastName, username, password);
            }
        }
    }



    @FXML
    public void onSignUpButtonClicked(ActionEvent event) throws IOException
    {
        String first_Name = txtF_FirstName.getText().trim();
        String last_Name = txtF_LastName.getText().trim();
        String username = txtF_Username.getText().trim();
        String password = txtF_Password.getText().trim();
        String confirm_Password = txtF_ConfirmPassword.getText().trim();


        if(checkField(first_Name, last_Name, username, password, confirm_Password) && checkPasswords(password, confirm_Password)){
            Parent root = FXMLLoader.load(getClass().getResource("../ClientUI/SignIn.fxml"));
            Scene scene = btn_SignIn.getScene();
            Parent root1 = anchorPane;

            root.translateXProperty().set(scene.getWidth() / 2);
            root1.translateXProperty().set(0);
            parentContainerSignUp.getChildren().add(root);

            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);
            KeyValue keyValue1 = new KeyValue(root1.translateXProperty(), -300, Interpolator.EASE_IN);
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.3), keyValue1);
            timeline.getKeyFrames().add(keyFrame);
            timeline.getKeyFrames().add(keyFrame1);
            timeline.setOnFinished(event1 -> {
                parentContainerSignUp.getChildren().remove(anchorPane);
            });
            timeline.play();
        }
        else
        {
            System.out.println("Registering new user");
            registerNewUser(first_Name, last_Name, username, password);
        }

    }


    private void registerNewUser(String firstName, String lastName, String username, String password)
    {
        // register new user and go to main menu scene
        String registerInfo = firstName + " " + lastName + " " + username + " " + password;

        Packet packet = new Packet(Packet.REGISTER_CLIENT, controller.getClient().getUserInformation(), registerInfo);
        controller.getClient().addRequestToServer(packet);
    }


    @FXML
    public void onSignInButtonClicked(ActionEvent event) throws IOException {
        // The middle anchorpane of the borderpane
        AnchorPane middleAnchorPane = signInController.getMiddleAnchorPane();

        // The stackpane of the anchorpane
        Pane root = signInController.getSignInPane();
        Scene scene = btn_SignIn.getScene();

        Pane root1 = parentContainerSignUp;

        root.translateXProperty().set(scene.getWidth() * -0.5);
        root1.translateXProperty().set(0);

        middleAnchorPane.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);
        KeyValue keyValue1 = new KeyValue(root1.translateXProperty(), 300, Interpolator.EASE_IN);
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.3), keyValue1);
        timeline.getKeyFrames().add(keyFrame);
        timeline.getKeyFrames().add(keyFrame1);
        timeline.setOnFinished(event1 -> {
            middleAnchorPane.getChildren().remove(parentContainerSignUp);
        });
        timeline.play();
    }

    public boolean checkPasswords(String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            usernameError.setTextFill(Color.RED);
            passwordError.setText("Passwords do not match");
            return false;
        }
        else
            return true;
    }
    public boolean checkField(String firstName,String lastName,String username,String password,String confirmPassword){
        boolean value_entered = true;
        if (firstName.isBlank()) {
            txtF_FirstName.setStyle("-fx-border-color: red;");
            firstNameError.setStyle("-fx-text-fill: red;");
            value_entered = false;
        } else{
            txtF_FirstName.setStyle("");
            firstNameError.setStyle("-fx-text-fill: white;");
            value_entered = false;
        }
        if (lastName.isBlank()) {
            txtF_LastName.setStyle("-fx-border-color: red;");
            lastNameError.setStyle("-fx-text-fill: red;");
            value_entered = false;
        } else{
            txtF_LastName.setStyle("");
            lastNameError.setStyle("-fx-text-fill: white;");
            value_entered = false;
        }
        if (username.isBlank()) {
            txtF_Username.setStyle("-fx-border-color: red;");
            usernameError.setStyle("-fx-text-fill: red;");
            value_entered = false;
        } else{
            txtF_Username.setStyle("");
            usernameError.setStyle("-fx-text-fill: white;");
            value_entered = false;
        }
        if (password.isBlank()) {
            txtF_Password.setStyle("-fx-border-color: red;");
            passwordError.setStyle("-fx-text-fill: red;");
            value_entered = false;
        } else{
            txtF_Password.setStyle("");
            passwordError.setStyle("-fx-text-fill: white;");
            value_entered = false;
        }
        if (confirmPassword.isBlank()) {
            txtF_ConfirmPassword.setStyle("-fx-border-color: red;");
            confirmPasswordError.setStyle("-fx-text-fill: red;");
            value_entered = false;
        } else{
            txtF_ConfirmPassword.setStyle("");
            confirmPasswordError.setStyle("-fx-text-fill: white;");
            value_entered = false;
        }

        return value_entered;
    }

    public void setClientController(ClientController controller) {
        this.controller = controller;
    }

    public void setSignInController(SignInController signInController) {
        this.signInController = signInController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Sign up Initialized");
    }

    @Override
    public void updateSignInResult(String message)
    {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("message: " + message);
            }
        });
    }
}