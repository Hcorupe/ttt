package UI.Client;

import Client.Client;
import Client.ClientController;
import Shared.Packet;
import Shared.UserInformation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private Button playButton, historyButton, exitButton, settingsButton;


    private ClientController clientController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onPlayButtonClicked(ActionEvent event) throws IOException {

        System.out.println("on play button clicked: ");
        System.out.println("get is deleted: " + clientController.getAccountClient().getUserInformation().getIsDeleted());

        if (clientController.getAccountClient().getUserInformation().getIsDeleted() == 0) {
            Stage stage = null;
            Parent root = null;

            if (event.getSource() == playButton) {
                stage = (Stage) playButton.getScene().getWindow();
                root = clientController.getLobbyPane();
            }
            stage.setScene(root.getScene());
            stage.show();

        } else {
            System.out.println("need an active account for this");
        }
    }


    @FXML
    public void onGameHistoryClicked(ActionEvent event) {
        Stage stage = null;
        Parent root = null;

        if (event.getSource() == historyButton) {
            stage = (Stage) historyButton.getScene().getWindow();
            root = clientController.getGameHistoryPane();
        }
        stage.setScene(root.getScene());
        stage.show();

        clientController.getGameHistoryController().GetGameinfo();
    }


    @FXML
    public void onOptionsButtonClicked(ActionEvent event) throws IOException {
        Stage stage = null;
        Parent root = null;

        if (event.getSource() == settingsButton) {
            stage = (Stage) settingsButton.getScene().getWindow();
            root = clientController.getOptionsPane();
        }
        stage.setScene(root.getScene());
        stage.show();
    }

    @FXML
    public void onExitButtonClicked(ActionEvent event) {
        Packet packet = new Packet(Packet.SIGN_OUT, clientController.getAccountClient().getUserInformation(), "SIGN-OUT");
        clientController.getAccountClient().addRequestToServer(packet);
        clientController.getGameClient().addRequestToServer(packet);
        clientController.stop();
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }


    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
}
