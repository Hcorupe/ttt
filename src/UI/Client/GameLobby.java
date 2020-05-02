package UI.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Client.ClientController;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class GameLobby implements Initializable {
    @FXML
    private Button NewGameButton;
    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
       
    public void onPlayButtonClicked(ActionEvent event) {

        if (event.getSource() == NewGameButton) {

        }

    }
}
