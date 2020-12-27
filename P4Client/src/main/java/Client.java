
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;


public class Client{
    Integer GameVal;

    Integer Port_Number;
    String  IP_Address;
    Socket  Socket_01;

    ObjectOutputStream Output_Stream;
    ObjectInputStream  Input_Stream;

    Boolean HasGuessedWord = false;

    ListView<String> DisplayListItems = new ListView<String>( );
    Consumer<Serializable> CallBack;

    String Selected_Category;
    HBox HBox_Categories = new HBox();

    public Client(String IP, Integer port){
        IP_Address  = IP;
        Port_Number = port;
    }

    void Start( ) throws UnknownHostException, IOException{
        // try{
            Socket_01 = new Socket(IP_Address, Port_Number);
            System.out.println("  >> Connection to server established. ");

            Output_Stream     = new ObjectOutputStream(Socket_01.getOutputStream( ));
            Input_Stream      = new ObjectInputStream(Socket_01.getInputStream( ));
            Socket_01.setTcpNoDelay(true);

        // }catch(Exception ex){
        //     System.out.println(" >> Client Exception: " + ex.getMessage());
        // }

        CallBack = new Consumer<Serializable>( ) {
            public void accept(Serializable data){
                Platform.runLater(( )->{
                    DisplayListItems.getItems( ).add(data.toString());
                });
            }
        };
    }


    HBox Get_HBox_ButtonCategories_ReceiveAndSend( ){
        HBox_Categories = new HBox();
        Receive_RemainingCategories( ).forEach(e->{
            HBox_Categories.getChildren().add(new Button(e));
        });

        return HBox_Categories;
    }

    TextField Get_LetterTextField( ){
        TextField txtField = new TextField();

        txtField.setOnKeyPressed(key->{
            if(key.getCode().equals(KeyCode.ENTER))
                GuessTheWord(txtField.getText().charAt(0));
        });

        return txtField;
    }


    Boolean GuessTheWord(Character letter){
        while(!Receive_HasGuessedTheWord( )){
            //Receive_GuessedLetters().forEach(e->System.out.print(e + " "));
            //System.out.println( );
            Receive_GuessedLetters().forEach(e->PRINT_TO_CLIENT_LIST(e + " "));
            Send_Letter(letter);

            Integer wrongGuess = Receive_NumInvalidLettersEntered( );
            PRINT_TO_CLIENT_LIST(" [Errors Made] = " + wrongGuess + " out 6.");
            if(wrongGuess.equals(6)){
                Receive_HasGuessedTheWord( );
                return false;
            }

        }
        return true;
    }


    //* HELPER METHODS
    public <T> void PRINT_TO_CLIENT_LIST(T msg){
        CallBack.accept(msg.toString( ));
    }

    public void SENDER_HELPER_FUNCTION(SerializedData sData, String exMsg){
        try{
            Output_Stream.writeObject(sData);
        }catch(Exception ex){
            System.out.println(exMsg);
        }
    }

    public String FORMATTED_CLIENT_MESSAGE(String msg) {return " >> [CLIENT]: " + msg + "\n";}

    //* SERIALIZED DATA SENDERS
    public void Send_Message(String msg){
        SerializedData sData = new SerializedData( );
        sData.Message = msg;
        this.SENDER_HELPER_FUNCTION(sData, 
        FORMATTED_CLIENT_MESSAGE("Failed to send [sData.Message] to the server host."));
    }

    public void Send_SentCategoryIsValid(Boolean val){
        SerializedData sData = new SerializedData( );
        sData.SentCategoryIsValid = val;
        this.SENDER_HELPER_FUNCTION(sData, 
        FORMATTED_CLIENT_MESSAGE("Failed to send [sData.SentCategoryIsValid] to the server host."));
    }

    public void Send_SelectedCategory(String category){
        this.SENDER_HELPER_FUNCTION(new SerializedData(category),
        FORMATTED_CLIENT_MESSAGE("Failed to send [sData.SelectedCategory] to server host."));
    }

    public void Send_Letter(Character letter){
        this.SENDER_HELPER_FUNCTION(new SerializedData(letter),
        FORMATTED_CLIENT_MESSAGE("Failed to send [sDataLetter] to client."));
    }

    public void Send_GuessedLetters(List<Character> list){
        this.SENDER_HELPER_FUNCTION(new SerializedData(list), 
        FORMATTED_CLIENT_MESSAGE("Failed to send [sData.GuessedLetters] to client."));
    }

    public void Send_HasGuessedTheWord(Boolean val){
        this.SENDER_HELPER_FUNCTION(new SerializedData(val), 
        FORMATTED_CLIENT_MESSAGE("Failed to send [sData.HasGuessedTheWord] to cleint."));
    }

    public void Send_NumInvalidLettersEntered(Integer val){
        this.SENDER_HELPER_FUNCTION(new SerializedData(val), 
        FORMATTED_CLIENT_MESSAGE("Failed to send [sData.NumInvalidLettersEntered] to cleint."));
    }

    //* SERIALIZED DATA RECEIVERS
    public String Receive_Message( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.Message;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.Message] from server host.");
            return null;
        }
    }

    public Boolean Receive_SentCategoryIsValid( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.SentCategoryIsValid;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.SentCategoryIsValid] from server host.");
            return null;
        }
    }

    public String Receive_SelectedCategory( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.SelectedCategory;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.SelectedCategory] from server host.");
            return null;
        }
    }

    public Character Receive_Letter( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.Letter;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.Letter] from server host.");
            return null;
        }
    }

    public List<Character> Receive_GuessedLetters( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            //this.PRINT_TO_CLIENT_LIST(sData.GuessedLetters.get(0));
            //this.CallBack.accept(sData.GuessedLetters.get(0).toString( ));
            return sData.GuessedLetters;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.GuessedLetters] from server host.");
            return null;
        }
    }

    public Boolean Receive_HasGuessedTheWord( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.HasGuessedTheWord;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.HasGuessedTheWord] from server host.");
            return null;
        }
    }

    public Integer Receive_NumInvalidLettersEntered( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.NumInvalidLettersEntered;

            }catch(Exception ex){
                FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.NumInvalidLettersEntered] from server host.");
                return null;
            }
    }

    public ArrayList<String> Receive_RemainingCategories( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.RemainingCategories;
        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to reveive [sData.RemainingCategories] from server host.");
            return null;
        }
    }

    public Integer Receive_GameOverVal( ){
        try{
            SerializedData sData = (SerializedData)Input_Stream.readObject( );
            return sData.GameOverVal;

        }catch(Exception ex){
            FORMATTED_CLIENT_MESSAGE("Failed to receive [sData.GameOverVal] from server host.");
            return null;
        }
    }
}