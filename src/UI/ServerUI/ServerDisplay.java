package UI.ServerUI;

import AccountService.AccountService;
import Client.ClientController;
import DataBase.sql.DataSource;
import DataBase.sql.DatabaseManager;
import GameService.GameService;
import Models.Game;
import Shared.GameInformation;
import Shared.UserInformation;
import ObserverPatterns.ServiceListener;
import Shared.Packet;
import com.mysql.cj.conf.StringProperty;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerDisplay implements Initializable, ServiceListener {

    public static GameService instance = null;
    @FXML
    private TableView<Game> activeGames;
    @FXML
    private TableView<GameInformation> games;
    @FXML
    private TableColumn<Game, String> gameID_AG, player1_AG, player2_AG, gameID_G, player1_G, player2_G, startTime_G, endTime_G, result_G, spectators_G;
    @FXML
    private TableColumn<Game, Timestamp> startTime_AG;
    @FXML
    private TableView<UserInformation> activePlayers, accounts;
    @FXML
    private TableColumn<UserInformation, String> username_AP, playerId_AP, username_A, password_A, firstName_A, lastName_A;
    @FXML
    private TableColumn<UserInformation, Integer> deleted_A;

    private ClientController clientController;
    private DatabaseManager ds = DatabaseManager.getInstance();
    private BlockingQueue<Packet> packetsReceived = new LinkedBlockingQueue<>();
    private ObservableList<Game> activeGamesList = FXCollections.observableArrayList();
    private ObservableList<GameInformation> allGamesList = FXCollections.observableArrayList();
    private ObservableList<UserInformation> onlinePlayersList = FXCollections.observableArrayList();
    private ObservableList<UserInformation> allPlayersList = FXCollections.observableArrayList();
    private ServiceListener serviceListener = AccountService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAGTable();
        initializeAPTable();
        initializeGTable();
        initializeATable();
    }

    private void initializeAGTable() {
        activeGames.setItems(activeGamesList);
        gameID_AG.setCellValueFactory(new PropertyValueFactory<>("id"));
        player1_AG.setCellValueFactory(new PropertyValueFactory<>("player1Username"));
        player2_AG.setCellValueFactory(new PropertyValueFactory<>("player2Username"));
        startTime_AG.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    }

    private void initializeAPTable() {
        activePlayers.setItems(onlinePlayersList);
        username_AP.setCellValueFactory(new PropertyValueFactory<>("username"));
        playerId_AP.setCellValueFactory(new PropertyValueFactory<>("id"));
    }

    private void initializeGTable() {
        games.setItems(allGamesList);
        gameID_G.setCellValueFactory(new PropertyValueFactory<>("id"));
        player1_G.setCellValueFactory(new PropertyValueFactory<>("player1Username"));
        player2_G.setCellValueFactory(new PropertyValueFactory<>("player2Username"));
        startTime_G.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime_G.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        result_G.setCellValueFactory(new PropertyValueFactory<>("winningPlayerId"));
        spectators_G.setCellValueFactory(new PropertyValueFactory<>("spectators"));
        try {
            allGamesList.addAll(ds.getAllGamesInfo());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeATable() {
        accounts.setItems(allPlayersList);
        username_A.setCellValueFactory(new PropertyValueFactory<>("username"));
        username_A.setCellFactory(TextFieldTableCell.forTableColumn());
        password_A.setCellValueFactory(new PropertyValueFactory<>("password"));
        password_A.setCellFactory(TextFieldTableCell.forTableColumn());
        firstName_A.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstName_A.setCellFactory(TextFieldTableCell.forTableColumn());
        lastName_A.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastName_A.setCellFactory(TextFieldTableCell.forTableColumn());
        deleted_A.setCellValueFactory(new PropertyValueFactory<>("isDeleted"));
        editableACols();
        try {
            allPlayersList.addAll(ds.AllUserInfo());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void editableACols() {
        accounts.setEditable(true);
    }


    @FXML
    public void onOnlinePlayerClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String username = activePlayers.getSelectionModel().getSelectedItem().toString();
            System.out.println("username selected: " + username);
        }
    }

    @FXML
    public void onAccountClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String username = activePlayers.getSelectionModel().getSelectedItem().toString();
            System.out.println("username selected: " + username);

        }
    }

    @FXML
    public void onActiveGameClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String game = activeGames.getSelectionModel().getSelectedItem().toString();
            System.out.println("game selected: " + game);
        }
    }

    @FXML
    public void onAllGameClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String game = games.getSelectionModel().getSelectedItem().toString();
            System.out.println("game selected: " + game);
        }
    }

    public void display(ActionEvent event) {

    }

    @Override
    public void onDataChanged(Packet packet) {
        packetsReceived.add(packet);
        updateUI();
    }

    private synchronized void updateUI() {
        Platform.runLater(() -> {
            Packet packet;
            try {
                packet = packetsReceived.take();

                switch (packet.getRequest()) {
                    case Packet.REGISTER_CLIENT:
                        UserInformation newRegisteredClient = (UserInformation) packet.getData();
                        System.out.println("<--- New Registered User: " + newRegisteredClient.getUsername() + " --->");
                        allPlayersList.add(newRegisteredClient);


                    case Packet.SIGN_IN:
                        UserInformation newSignedInUser = (UserInformation) packet.getData();
                        System.out.println("<--- New Signed In User: " + newSignedInUser.getUsername() + " --->");
                        onlinePlayersList.add(newSignedInUser);
                        break;

                    case Packet.SIGN_OUT:
                        UserInformation newSignedOutUser = (UserInformation) packet.getData();
                        System.out.println("<--- New Signed Out User: " + newSignedOutUser.getUsername() + " --->");
                        onlinePlayersList.remove(newSignedOutUser);
                        break;

                    case Packet.ACTIVE_GAME:
                        Game newCreatedGame = (Game) packet.getData();
                        System.out.println("<--- New Created Game: " + newCreatedGame.getId() + " --->");
                        System.out.println("Start Time: " + newCreatedGame.getStartTime());
                        activeGamesList.add(newCreatedGame);
                        break;

                    case Packet.GAME_CLOSE:
                        Game newClosedGame = (Game) packet.getData();
                        System.out.println("<--- New Closed Game: " + newClosedGame.getId() + " --->");
                        activeGamesList.remove(newClosedGame);
                        GameInformation information = new GameInformation();
                        information.setId(newClosedGame.getId());
                        information.setPlayer1Username(newClosedGame.getPlayer1Username());
                        information.setPlayer2Username(newClosedGame.getPlayer2Username());
                        information.setStartTime(newClosedGame.getStartTime());
                        information.setEndTime(newClosedGame.getEndTime());
                        information.setWinningPlayerId(newClosedGame.getWinningPlayerId());
                        information.setStartingPlayerId(newClosedGame.getStartingPlayerId());
                        information.setSpectators(newClosedGame.getSpectators());
                        allGamesList.add(information);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void notifyAccountsServer(Packet packet) {
        serviceListener.onDataChanged(packet);
    }

    private class TextFieldCellFactory implements Callback<TableColumn<UserInformation, String>, TableCell<UserInformation, String>> {

        @Override
        public TableCell<UserInformation, String> call(TableColumn<UserInformation, String> userInformationStringTableColumn) {
            TextFieldCell textFieldCell = new TextFieldCell();
            return textFieldCell;
        }

        public class TextFieldCell extends TableCell<UserInformation, String> {
            private TextField textField;
            private SimpleStringProperty boundToCurrently = null;

            public TextFieldCell() {
                String strCss;
                strCss = "-fx-padding: 0";
                this.setStyle(strCss);

                textField = new TextField();
                this.setGraphic(textField);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty) {
                    this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ObservableValue<String> ov = getTableColumn().getCellObservableValue(getIndex());
                    SimpleStringProperty sp = (SimpleStringProperty) ov;

                    if(this.boundToCurrently == null) {
                        this.boundToCurrently = sp;
                        this.textField.textProperty().bindBidirectional(sp);
                    } else {
                        if(this.boundToCurrently != sp) {
                            this.textField.textProperty().unbindBidirectional(this.boundToCurrently);
                            this.boundToCurrently = sp;
                            this.textField.textProperty().bindBidirectional(this.boundToCurrently);
                        }
                    }
                } else {
                    this.setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }

        }
    }
}
