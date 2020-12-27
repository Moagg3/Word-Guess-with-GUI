package ServerCode;

import java.io.FileWriter;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

// ToDo 1: A way to enter to port to listen to. ------------------- [DONE]
// ToDo 2: A way to start the server. ----------------------------- [DONE]
// ToDo 3: A server log that reports on what the server is doing.-- [DONE]

// http://www.naturalborncoder.com/java/javafx/2013/06/13/samplefx-updating-a-label/
// https://stackoverflow.com/questions/13227809/displaying-changing-values-in-javafx-label
public class WordGuessServer extends Application {

    public static void main(String[] args) {launch(args);}
    
    BorderPane BorderPane_01= new BorderPane( );

    Server server;
    static Integer XPos = 200;
    static Integer YPos = 100;

    Integer ThemeVal; 
    // 0: Dark Mode
    // 1: Light Mode

    Scene ServerScene;

	@Override
	public void start(Stage primaryStage) throws Exception {
        ThemeVal = 0;
        IntroScene(primaryStage);
    }

    void IntroScene(Stage primaryStage){
        BorderPane pane = new BorderPane();
        TextField textField = new TextField("Enter Port Number");
        Button b1 = new Button("Start");
        primaryStage.setResizable(false);

        HBox hbox = new HBox(5);
        hbox.getChildren( ).addAll(textField, b1);
        
        hbox.setAlignment(Pos.CENTER);
        pane.setCenter(hbox);
        
        b1.setOnAction(e->{
            if(isNumeric(textField.getText())){
                Integer portVal = Integer.valueOf(textField.getText());
                server = new Server(portVal);
                server.Start( );
    
                // BorderPane_01 Dependent Methods
                SetTitle("THE SERVER");
                DISPLAY_LEFTPANE_WIDGET(primaryStage);
                DISPLAY_CENTERPANE_WIDGET( );
    
                ServerScene = new Scene(BorderPane_01, 800, 500);
                primaryStage.setTitle("Server Client Status Report Log");
                primaryStage.setScene(ServerScene);
                primaryStage.show();
            }

        });

        Scene scene = new Scene(pane, 350, 100);
        primaryStage.setTitle("Server Port Number Selection");
        primaryStage.setScene(scene);
        primaryStage.show( );

    }

    void SetTitle(String titleStr){
        Label title = new Label(titleStr);
        title.setFont(Font.font("Verdana", 20));
        

        HBox hbox_top = new HBox();
        hbox_top.getChildren().addAll(title);
        hbox_top.setAlignment(Pos.CENTER);

        if(ThemeVal.equals(0)){
            hbox_SolidColorStylist(hbox_top, "#a3a3a3","#423d3d");
            title.setTextFill(Color.WHITESMOKE);
        }
        else if(ThemeVal.equals(1)){
            hbox_SolidColorStylist(hbox_top, "#f5ece4","#f5ece4");
            title.setTextFill(Color.BLACK);
        }


                            
        // hbox_top.setPrefHeight(90);
        // hbox_top.setMaxHeight(90);

        BorderPane_01.setTop(hbox_top);
    }

    VBox GetVbox_ClientStatusWidget(Stage primaryStage, String labelStr, String textFieldStr, String buttonStr){
        Label     label_01  = new Label(labelStr);
        label_01.setFont(Font.font("Verdana", 14));
        label_01.setTextFill(Color.BLACK);

    
        
        
        TextField textField = new TextField(textFieldStr); textField.setPrefWidth(70); textField.setMaxWidth(70);
        Button    b1        = new Button(buttonStr);
        Button    b2        = new Button("Show All Active Clients");

        // Integer key = Integer.valueOf(textField.getText());
        
        

        HBox txtFld_b1 = new HBox(5);
        txtFld_b1.getChildren().addAll(textField, b1, b2);

        VBox vBox = new VBox(5);
        vBox.getChildren().addAll(label_01, txtFld_b1);
        vBox.setPadding(new Insets(10, 10, 10, 20));
        vBox.setPrefWidth(400);
        vBox.setMaxWidth(400);

        String cssLayout = "-fx-border-color: #000000;\n" +
                   "-fx-border-insets: 5;\n" +
                   "-fx-border-width: 3;\n" +
                   "-fx-border-style: solid ;\n" +
                   "-fx-background-color:linear-gradient(from 10% 10% to 100% 100%, #e3c291, #e39c32)";

        vBox.setStyle(cssLayout);

        b1.setOnAction(e->{
            if(isNumeric(textField.getText())){
                if(!server.ClientThread_IsActive(Integer.valueOf(textField.getText()))){
                    server.SERVER_REPORT("Entered client ID is not active.");
                }
                else{
                    if(vBox.getChildren().size() >= 3){
                        vBox.getChildren().remove(vBox.getChildren().size()-1);
                    }
                    vBox.getChildren( ).add(server.Get_ClientThread_DisplayList(Integer.valueOf(textField.getText())));

                    
                    server.Get_ClientThread_DisplayList(Integer.valueOf(textField.getText())).setOnMouseClicked(event->{
                        ShowClientStatus(Integer.valueOf(textField.getText()), primaryStage);
                    });
                }
            }
            else{
                server.SERVER_REPORT("Invalid entry. You must enter a client number.");
            }
        });

        b2.setOnAction(e->{

            if(server.MAX_CLIENTTHREAD_COLLECTED.equals(0))
                server.SERVER_REPORT("No clients are currently active.");
            else
            {
                for(int i = 1; i <= server.MAX_CLIENTTHREAD_COLLECTED; ++i){
                    if(server.ClientThread_IsActive(i)){
                        ShowClientStatus(i, primaryStage);
                    }
                }
            }
        });
        
        return vBox;
    }

    void DISPLAY_LEFTPANE_WIDGET(Stage primaryStage){
        BorderPane_01.setLeft(GetVbox_ClientStatusWidget(primaryStage, "Enter Client Number", "", "Display"));
    }
    
    VBox GetVbox_ServerReportWidget(String label_str){
        Label label = new Label(label_str);
        label.setFont(Font.font("Verdana", 14));
        label.setTextFill(Color.BLACK);

        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(label, server.Get_ServerDisplayList( ));
        vbox.setPrefWidth(400);
        vbox.setMaxWidth(400);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox_GradientColorStylist(vbox, "#000000" ,"#ffaf69", "#854610");
        return vbox;
    }

    void DISPLAY_CENTERPANE_WIDGET( ){
        BorderPane_01.setCenter(GetVbox_ServerReportWidget("Server Report"));
    }

    // https://o7planning.org/en/11533/opening-a-new-window-in-javafx
    void  ShowClientStatus(Integer ClientKey, Stage primaryStage){
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(server.Get_ClientThread_DisplayList(ClientKey));
        Scene secondScene = new Scene(secondaryLayout, 300, 400);

        // New window (Stage)
        Stage newWindow = new Stage( );
        newWindow.setTitle("Client " + ClientKey + " Status Log");
        newWindow.setScene(secondScene);

        // Set position of second window, related to primary window.
        newWindow.setX(primaryStage.getX( ) + (XPos += 20));
        newWindow.setY(primaryStage.getY( ) + (YPos += 20));

        newWindow.show( );
    }

    public static boolean isNumeric(final String str) {
        if (str == null || str.length( ) == 0){
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }

    void vbox_GradientColorStylist(VBox vBox, String borderColor,String color1, String color2){
        String cssLayout = "-fx-border-color: " + borderColor +";\n" +
                   "-fx-border-insets: 5;\n" +
                   "-fx-border-width: 3;\n" +
                   "-fx-border-style: solid ;\n" +
                   "-fx-background-color:linear-gradient(from 10% 10% to 100% 100%, "+ color1 +", "+ color2 + ")";

        vBox.setStyle(cssLayout);
    }

    void vbox_SolidColorStylist(VBox vBox, String borderColor, String color1){
        String cssLayout =  "-fx-border-color: " + borderColor +";\n" +
                            "-fx-border-insets: 5;\n" +
                            "-fx-border-width: 3;\n" +
                            "-fx-border-style: solid ;\n" +
                            "-fx-background-color: " + color1;
        vBox.setStyle(cssLayout);
    }

    void hbox_GradientColorStylist(HBox hBox, String borderColor, String color1, String color2){
        String cssLayout = "-fx-border-color: " + borderColor +";\n" +
                   "-fx-border-insets: 5;\n" +
                   "-fx-border-width: 3;\n" +
                   "-fx-border-style: solid ;\n" +
                   "-fx-background-color:linear-gradient(from 10% 10% to 100% 100%, "+ color1 +", "+ color2 + ")";

        hBox.setStyle(cssLayout);
    }

    void hbox_SolidColorStylist(HBox hBox, String borderColor, String color1){
        String cssLayout =  "-fx-border-color: " + borderColor +";\n" +
                            "-fx-border-insets: 5;\n" +
                            "-fx-border-width: 3;\n" +
                            "-fx-border-style: solid ;\n" +
                            "-fx-background-color: " + color1;
        hBox.setStyle(cssLayout);
    }

}
