import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class WordGuessClient extends Application {

    public static String VERSION = "v1.0";

	public static void main(String[] args) {launch(args);}

    Client client_connection;
    Integer ErrorMade = 0;
    Label ErrorMade_label = new Label( );
    Integer temp = 0;
    HBox hbox_letters;

    ListView<String> TheList = new ListView<String>( );
    ObservableList<String> items = FXCollections.observableArrayList( );

    HBox hbox_buttons = new HBox( );
    VBox vbox_centerPane = new VBox(5);
    TextField txtField_letter = new TextField("Type Letter");
    HBox hbox_Categories = new HBox(5);
    VBox vbox_clientInfo = new VBox(5);

    Scene scene1;

    String client_ID = "";
    String client_IP = "";
    String client_Port = "";

    VBox VBox_ClientInfo(String ID, String IP, String Port){
        Label id   = new Label("CLIENT ID: "  + ID);
        Label ip   = new Label("IP ADDRESS: "  + IP);
        Label port = new Label("PORT NUMBER: " + Port);

        vbox_clientInfo.getChildren().addAll(id, ip, port);
        vbox_clientInfo.setPadding(new Insets(5, 5, 5, 5));
        //new Insets(top, right, bottom, left)

        this.vbox_GradientColorStylist(vbox_clientInfo, "", "#fff7eb", "#ffe1b5");
        vbox_clientInfo.setPrefHeight(30);
        vbox_clientInfo.setMaxHeight(30);
        vbox_clientInfo.setAlignment(Pos.BOTTOM_LEFT);

        return vbox_clientInfo;
    }

    void LogIn(Stage primaryStage){
        BorderPane pane = new BorderPane( );
        Scene logInScene = new Scene(pane, 300, 100);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Log in");
        primaryStage.setScene(logInScene);
        primaryStage.show( );

        TextField textField_IP = new TextField("Enter IP Address");
        TextField textField_Port = new TextField("Enter Port Number");

        Button login_button = new Button("Login");
        
        VBox vbox_txtfields = new VBox(5);
        vbox_txtfields.getChildren( ).addAll(textField_IP, textField_Port, login_button);
        pane.setCenter(vbox_txtfields);
        pane.setPadding(new Insets(10, 10, 10, 10));

        login_button.setOnAction(e->{
            try{
                client_connection = new Client(textField_IP.getText( ), Integer.valueOf(textField_Port.getText( ).toString( )));
                client_connection.Start( );

                // Receive the sent client ID from server host
                this.client_ID   = client_connection.Receive_Message( );

                this.client_IP   = textField_IP.getText();
                this.client_Port = textField_Port.getText();

                System.out.println("Loading Client . . . ");
                primaryStage.setTitle("Guess that word! " + VERSION);
                GameStarts(primaryStage);
            }catch(Exception ex){
                System.out.println("Invalid IP or Port number value entry. Try again.");

                this.InvalidLogInAlertBox("Connection to server was unsuccessful. Try again.", 
                                           textField_IP.getText(),
                                           textField_Port.getText(),
                                           primaryStage);

                textField_IP.setText("Enter IP Address");
                textField_Port.setText("Enter Port Number");
            }
        });

        login_button.setOnKeyPressed(key->{
            if(key.getCode( ).equals(KeyCode.ENTER)){
                try{
                    client_connection = new Client(textField_IP.getText( ), Integer.valueOf(textField_Port.getText( ).toString( )));
                    client_connection.Start( );

                    // Receive the sent client ID from server host
                    this.client_ID   = client_connection.Receive_Message( );
                    
                    this.client_IP   = textField_IP.getText();
                    this.client_Port = textField_Port.getText();

                    System.out.println("Loading Client . . . ");
                    primaryStage.setTitle("Guess that word! " + VERSION);
                    GameStarts(primaryStage);
                }catch(Exception ex){
                    System.out.println("Invalid IP or Port number value entry. Try again.");

                    this.InvalidLogInAlertBox("Connection to server was unsuccessful. Try again.", 
                                               textField_IP.getText(),
                                               textField_Port.getText(),
                                               primaryStage);

                    textField_IP.setText("Enter IP Address");
                    textField_Port.setText("Enter Port Number");
                }
            }
        });
    }

    String IP;
    Integer Port;

	@Override
	public void start(Stage primaryStage) throws Exception {
        LogIn(primaryStage);
    }

    void GameStarts(Stage primaryStage){
        BorderPane pane1 = new BorderPane( );
        scene1 = new Scene(pane1, 900, 300);

        this.pane_GradientColorStylist(pane1, "#dc143c", "#661a33");
        pane1.setPadding(new Insets(30, 30, 30, 30));

        txtField_letter.setPrefWidth(80);
        txtField_letter.setMaxWidth(80);
        txtField_letter.setDisable(true);

        vbox_centerPane.getChildren( ).addAll(hbox_buttons, txtField_letter);
        vbox_centerPane.setAlignment(Pos.CENTER);

        items.addAll(client_connection.Receive_Message( ));
        pane1.setCenter(vbox_centerPane);
        hbox_buttons.setAlignment(Pos.CENTER);
        hbox_buttons.setSpacing(5);
        hbox_buttons.setPadding(new Insets(10, 10, 10, 10));

        hbox_Categories.setAlignment(Pos.TOP_CENTER);
        hbox_Categories.setSpacing(10);
        hbox_Categories.setPadding(new Insets(10, 10, 20, 10));


        pane1.setTop(hbox_Categories);

        client_connection.Receive_RemainingCategories( ).forEach(e->hbox_Categories.getChildren( ).add(get_styledButton_01(e)));

        for(int i = 0; i < hbox_Categories.getChildren( ).size( ); ++i){
            Button b1 = (Button)hbox_Categories.getChildren( ).get(i);
            b1.setOnAction(e->{
                client_connection.Send_SelectedCategory(b1.getText( ));
                items.clear( );
                client_connection.Receive_GuessedLetters( ).forEach(k->{
                    hbox_buttons.getChildren( ).add(this.get_styledButton_02(k.toString( )));
                });
                txtField_letter.setDisable(false);
                b1.setDisable(true);
            });
        }

        txtField_letter.setOnKeyPressed(key->{
            if(key.getCode( ).equals(KeyCode.ENTER)){
                client_connection.Send_Letter(txtField_letter.getText( ).charAt(0));
                String errorsMade = client_connection.Receive_NumInvalidLettersEntered( ).toString( );

                if(errorsMade.equals("6")){
                    items.clear( );
                    items.add("You failed to guess the word");
                    txtField_letter.setDisable(true);
                    hbox_Categories.getChildren( ).clear( );

                    Integer gameOverVal = client_connection.Receive_GameOverVal( );
                    if(gameOverVal.equals(0)){ //! GAME OVER; Client lost the game
                        items.add(client_connection.Receive_Message( ));
                        GameOverAlertBox(client_connection.Receive_Message( ), primaryStage);
                        //***************************************** */
                        items.clear( );
                        hbox_Categories.getChildren( ).clear( );
                        hbox_buttons.getChildren( ).clear( );

                        items.addAll(client_connection.Receive_Message( ));
                        client_connection.Receive_RemainingCategories( ).forEach(e->hbox_Categories.getChildren( ).add(get_styledButton_01(e)));
                        for(int i = 0; i < hbox_Categories.getChildren( ).size( ); ++i){
                            Button b1 = (Button)hbox_Categories.getChildren( ).get(i);
                            b1.setOnAction(e->{
                                client_connection.Send_SelectedCategory(b1.getText( ));
                                items.clear( );
                                client_connection.Receive_GuessedLetters( ).forEach(k->{
                                    hbox_buttons.getChildren( ).add(this.get_styledButton_02(k.toString( )));
                                });
                                txtField_letter.setDisable(false);
                                b1.setDisable(true);
                            });
                        }
                        //***************************************** */
                    }
                    else{
                        items.add(client_connection.Receive_Message( ));
                        client_connection.Receive_RemainingCategories( ).forEach(e->hbox_Categories.getChildren( ).add(get_styledButton_01(e)));

                        for(int i = 0; i < hbox_Categories.getChildren( ).size( ); ++i){
                            Button b1 = (Button)hbox_Categories.getChildren( ).get(i);
                            b1.setOnAction(e->{
                                client_connection.Send_SelectedCategory(b1.getText( ));
                                items.clear( );
                                client_connection.Receive_GuessedLetters( ).forEach(k->{
                                    hbox_buttons.getChildren( ).add(this.get_styledButton_02(k.toString( )));
                                });
                                txtField_letter.setDisable(false);
                                b1.setDisable(true);
                            });
                        }
                        hbox_buttons.getChildren( ).clear( );
                    }
                }
                else{
                    txtField_letter.clear( );
                    items.clear( );
                    hbox_buttons.getChildren( ).clear( );
                    client_connection.Receive_GuessedLetters( ).forEach(e->{
                        hbox_buttons.getChildren( ).add(this.get_styledButton_02(e.toString( )));
                    });
                    items.add("Errors made: (" + errorsMade + "/6)");

                    if(client_connection.Receive_HasGuessedTheWord( )){
                        items.add("You guessed it!");
                        items.clear( );
                        txtField_letter.setDisable(true);
                        hbox_Categories.getChildren( ).clear( );

                        Integer gameOverVal = client_connection.Receive_GameOverVal( );
                        if(gameOverVal.equals(1)){ //! GAME OVER, Client won the game
                            items.add(client_connection.Receive_Message( ));
                            GameOverAlertBox(client_connection.Receive_Message( ), primaryStage);
                            //***************************************** */
                            items.clear( );
                            hbox_Categories.getChildren( ).clear( );
                            hbox_buttons.getChildren( ).clear( );

                            items.addAll(client_connection.Receive_Message( ));
                            client_connection.Receive_RemainingCategories( ).forEach(e->hbox_Categories.getChildren( ).add(get_styledButton_01(e)));
                            for(int i = 0; i < hbox_Categories.getChildren( ).size( ); ++i){
                                Button b1 = (Button)hbox_Categories.getChildren( ).get(i);
                                b1.setOnAction(e->{
                                    client_connection.Send_SelectedCategory(b1.getText( ));
                                    items.clear( );
                                    client_connection.Receive_GuessedLetters( ).forEach(k->{
                                        hbox_buttons.getChildren( ).add(this.get_styledButton_02(k.toString( )));
                                    });
                                    txtField_letter.setDisable(false);
                                    b1.setDisable(true);
                                });
                            }
                            //***************************************** */
                        }
                        else{
                            items.add(client_connection.Receive_Message( ));
                            client_connection.Receive_RemainingCategories( ).forEach(e->hbox_Categories.getChildren( ).add(get_styledButton_01(e)));
                            for(int i = 0; i < hbox_Categories.getChildren( ).size( ); ++i){
                                Button b1 = (Button)hbox_Categories.getChildren( ).get(i);

                                b1.setOnAction(e->{
                                    client_connection.Send_SelectedCategory(b1.getText( ));
                                    items.clear( );
                                    client_connection.Receive_GuessedLetters( ).forEach(k->{
                                        hbox_buttons.getChildren( ).add(this.get_styledButton_02(k.toString( )));
                                    });
                                    txtField_letter.setDisable(false);
                                    b1.setDisable(true);
                                });
                            }
                            hbox_buttons.getChildren( ).clear( );
                        }
                    }
                }
            }
        });

        TheList.setItems(items);
        pane1.setRight(TheList);
        pane1.setLeft(this.VBox_ClientInfo(this.client_ID, this.client_IP, this.client_Port));
        primaryStage.setScene(scene1);
        primaryStage.show( );
    }


    Alert GameOverAlertBox(String msg, Stage primaryStage){
        Alert  alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(msg);
        alert.setContentText("Choose one of the following.");

        ButtonType yes_button = new ButtonType("Yes, play again.");
        ButtonType no_button = new ButtonType("I'm done.", ButtonData.OK_DONE);

        alert.getButtonTypes( ).setAll(yes_button, no_button);

        Optional<ButtonType> result = alert.showAndWait( );
        if(result.get( ).equals(yes_button)){
            client_connection.Send_Message("play again");
        }
        else if(result.get( ).equals(no_button)){
            client_connection.Send_Message("done");
            primaryStage.close( );
            System.exit(0);
        }

        return alert;
    }


    Alert InvalidLogInAlertBox(String msg, String IP, String Port, Stage primaryStage){
        Alert  alert = new Alert(AlertType.ERROR);
        alert.setTitle("Connection Failed");
        alert.setHeaderText(msg);
        alert.setContentText("Entered IP: "   + IP + "\n" +
                             "Entered Port: " + Port);

        ButtonType done_button = new ButtonType("DONE", ButtonData.OK_DONE);

        alert.getButtonTypes( ).setAll(done_button);

        Optional<ButtonType> result = alert.showAndWait( );
        if(result.get( ).equals(done_button)){
            alert.close();
        }
        return alert;
    }



    Boolean GuessTheWord(Client client_connection, Scanner scanner){
        while(!client_connection.Receive_HasGuessedTheWord( )){
            client_connection.Receive_GuessedLetters( ).forEach(e->System.out.print(e + " "));
            System.out.println( );
            client_connection.Send_Letter(scanner.nextLine( ).charAt(0));

            Integer wrongGuess = client_connection.Receive_NumInvalidLettersEntered( );
            System.out.println(" [Errors Made] = " + wrongGuess + " out 6.");
            if(wrongGuess.equals(6)){
                client_connection.Receive_HasGuessedTheWord( );
                return false;
            }

        }
        return true;
    }

    <T> void PrintToConsole(T msg){System.out.println(msg);}

    String ListToString(List<Character> list){
        String word = "";
        for (Character letter : list) {
            word += letter;
            word += ' ';
        }
        return word;
    }

    void vbox_GradientColorStylist(VBox vBox, String borderColor,String color1, String color2){
        String cssLayout = "-fx-border-color: " + borderColor +";\n" +
                           "-fx-border-insets: 5;\n" +
                           "-fx-border-width: 3;\n" +
                           "-fx-border-style: solid;\n" +
                           "-fx-background-color:linear-gradient(from 10% 10% to 100% 100%, "+ color1 +", "+ color2 + ")";
        vBox.setStyle(cssLayout);
    }

    void vbox_SolidColorStylist(VBox vBox, String borderColor, String color1){
        String cssLayout =  "-fx-border-color: " + borderColor +";\n" +
                            "-fx-border-insets: 5;\n" +
                            "-fx-border-width: 3;\n" +
                            "-fx-border-style: solid;\n" +
                            "-fx-background-color: " + color1;
        vBox.setStyle(cssLayout);
    }

    void hbox_GradientColorStylist(HBox hBox, String borderColor, String color1, String color2){
        String cssLayout = "-fx-border-color: " + borderColor +";\n" +
                           "-fx-border-insets: 5;\n" +
                           "-fx-border-width: 3;\n" +
                           "-fx-border-style: solid;\n" +
                           "-fx-background-color:linear-gradient(from 10% 10% to 100% 100%, "+ color1 +", "+ color2 + ")";
        hBox.setStyle(cssLayout);
    }

    void hbox_SolidColorStylist(HBox hBox, String borderColor, String color1){        
        String cssLayout =  "-fx-border-color: " + borderColor +";\n" +
                            "-fx-border-insets: 5;\n" +
                            "-fx-border-width: 3;\n" +
                            "-fx-border-style: solid;\n" +
                            "-fx-background-color: " + color1;
        hBox.setStyle(cssLayout);
    }

    void pane_GradientColorStylist(BorderPane pane, String color1, String color2){
        pane.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, " + color1 + ", " + color2 + ")");
    }

    Button get_styledButton_01(String button_str){
        Button button = new Button(button_str);
        button.setStyle("-fx-border-color: #444444;" +
                        "-fx-border-width: 2px;"     +
                        "-fx-background-color: #fffaeb;" +
                        "-fx-font-size: 2em;");
        return button;
    }

    Button get_styledButton_02(String button_str){
        Button button = new Button(button_str);
        button.setStyle("-fx-border-color: #444444;" +
                        "-fx-border-width: 2px;"     +
                        "-fx-background-color: ##e0fae6;" +
                        "-fx-font-size: 2em;");
        return button;
    }
}
