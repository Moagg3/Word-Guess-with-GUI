package ServerCode;

import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.lang.Thread;

// ToDo: Fix the creating the txt.file part
// ToDo: Play again or Quit

public class Server{

    public class NODE_ClienThread{
        ClientThread CT;
        Boolean IsActive;
        NODE_ClienThread(ClientThread ct, Boolean isActive){
            CT       = ct;
            IsActive = isActive;
        }
    }

    public ServerSocket       ServerSocket_01;
    public Integer            Port_Number;
    ClientThreadCollector CT_Collector;
    public Hashtable<Integer, NODE_ClienThread>  HT_ClientThread;
    public Integer            ThreadCounter;
    FileWriter ServerLog_txt;
    Integer LogMsgCounter;
    Boolean HasClientsConnected;
    public Consumer<Serializable> ServerCallBack;
    ListView<String> ServerListView = new ListView<String>( );
    Integer MAX_CLIENTTHREAD_COLLECTED;

    // Param. Constructor
    public Server(Integer _port){
        Port_Number = _port;
        ThreadCounter = 0;
        HT_ClientThread = new Hashtable<Integer, NODE_ClienThread>( );

        MAX_CLIENTTHREAD_COLLECTED = 0;

        try{
            ServerLog_txt = new FileWriter("ServerLog.txt");
        }catch(Exception ex){
            System.out.println("  >> [SERVER]: Exception thrown; failed to create server log text file.");
            ex.printStackTrace( );
        }
        LogMsgCounter = 0;

        CT_Collector = new ClientThreadCollector( );
        HasClientsConnected = false;

        ServerCallBack = new Consumer<Serializable>() {
            public void accept(Serializable data){
                Platform.runLater(( )->{
                    ServerListView.getItems( ).add( (++LogMsgCounter) + ". " + data.toString( ) );
                });
            }
        };

    }

    // Starts the run() method of the CT_Collector class which starts the process of actively collecting any connecting client threads.
    public void             Start( )                                     {CT_Collector.start( );}

    // Returns the ListView specific to the Server class
    public ListView<String> Get_ServerDisplayList( )                     {return ServerListView;}

    // Returns the ListView specific to the ClientThread; (Acquired using a key)
    public ListView<String> Get_ClientThread_DisplayList(Integer ht_key) {return HT_ClientThread.get(ht_key).CT.DisplayListItems;}

    // Determines if the Client Thread is an active one based on the provided key
    public Boolean  ClientThread_IsActive(Integer key) {
        if(!HT_ClientThread.containsKey(key)){
            return false;
        }
        return true;
    }

    // Prints out any server-related message to the console.
    public void PRINT_SERVER_MESSAGE(String msg)       {System.out.println("  >> [SERVER]: " + msg);}

    // Write the message to the server-log text file
    void WRITE_TO_SERVERLOG(String msg){
        try{
            ServerLog_txt.write(LogMsgCounter + ". [SERVER]: " + msg + "\n");
        }catch(Exception ex){
            PRINT_SERVER_MESSAGE("Failed to write [" + msg + "] to server log.");
        }
    }

    // Prints message to console
    // Writes message to Client<#>Log.txt
    // Prints the message to assigned Client Thread ListView Class Object
    public void PRINT_AND_WRITE_CONSOLE_CLTHREAD(Integer key, String msg){
        PRINT_SERVER_MESSAGE(msg);
        if(HT_ClientThread.containsKey(key))
            HT_ClientThread.get(key).CT.CallBack.accept(". [SERVER]: " + msg);
    }

    // Prints message to console
    // Write message to ServerLog.txt
    // Prints the message to Server's ListView
    public void SERVER_REPORT(String msg){
        PRINT_SERVER_MESSAGE(msg);
        WRITE_TO_SERVERLOG(msg);
        this.ServerCallBack.accept("[SERVER REPORT] " + msg);
    }

    // A class that collects the connecting client threads and puts them in a hashtable
    // Key: ThreadCounter; starts at value, 1
    public class ClientThreadCollector extends Thread{
        public void run( ){
            try{
                ServerSocket_01 = new ServerSocket(Port_Number);
                SERVER_REPORT("Server is waiting for a client");

                while(true){
                    ClientThread clientThread = new ClientThread(ServerSocket_01.accept( ), ++ThreadCounter);
                    MAX_CLIENTTHREAD_COLLECTED = ThreadCounter;
                    SERVER_REPORT("Server accepted has <Client " + ThreadCounter + ">.");
                    HT_ClientThread.put(ThreadCounter, new NODE_ClienThread(clientThread, true));
                    SERVER_REPORT("Current clients connected: " + HT_ClientThread.size( ));
                    clientThread.start( );
                    HasClientsConnected = true;
                }
            }catch(Exception ex){
                SERVER_REPORT("Server socket failed to accept client.");
                ex.printStackTrace( );
            }
        }
    }

    // A class that gets a thread and reserve it for a connecting client to server.
    public class ClientThread extends Thread{

        public Consumer<Serializable> CallBack;
        public Socket             Accepted_Socket;
        public Integer            Key_ThreadCount;
        public ObjectInputStream  Input_Stream;
        public ObjectOutputStream Output_Stream;

        Boolean IsDonePlaying = false;

        //* Client's game related data members
        String Client_Selected_Category = "n/a";

        Category Def_Category01;
        Category Def_Category02;
        Category Def_Category03;

        WordCategoryGenerator animalCategory = new AnimalCategoryGenerator( );
        WordCategoryGenerator peopleCategory = new PeopleCategoryGenerator( );
        WordCategoryGenerator foodCategory   = new FoodCategoryGenerator( );

        class NODE_CATEGORY{
            Category category;
            Boolean categoryIsDone = false;
            Integer numTriesLeft = 3;
            NODE_CATEGORY(Category _category){this.category = _category;}

        }

        Hashtable<String, NODE_CATEGORY> HT_NodeCategories = new Hashtable<String, NODE_CATEGORY>();

        ListView<String> DisplayListItems = new ListView<String>( );

        Integer MessageCounter = 0;

        public ClientThread(Socket _acceptedSocket, Integer _threadCounter){
            CallBack = new Consumer<Serializable>( ) {
                public void accept(Serializable data){
                    Platform.runLater(( )->{
                        DisplayListItems.getItems( ).add((++MessageCounter) + data.toString());
                    });
                }
            };


            this.Accepted_Socket = _acceptedSocket;
            this.Key_ThreadCount = _threadCounter;

            // Default Categories for the Server
            //* Uses the Abstract Factory Design Pattern
            Def_Category01 = animalCategory.createCategory("Aquatic"); // Aquatic Animals
            Def_Category02 = peopleCategory.createCategory("USPresidents");
            Def_Category03 = foodCategory.createCategory("Fruits");

            HT_NodeCategories.put(Def_Category01.Name, new NODE_CATEGORY(Def_Category01));
            HT_NodeCategories.put(Def_Category02.Name, new NODE_CATEGORY(Def_Category02));
            HT_NodeCategories.put(Def_Category03.Name, new NODE_CATEGORY(Def_Category03));
        }

        public void run( ){
            try{
                Output_Stream = new ObjectOutputStream(Accepted_Socket.getOutputStream( ));
                Input_Stream  = new ObjectInputStream(Accepted_Socket.getInputStream( ));
                Accepted_Socket.setTcpNoDelay(true);
            }catch(Exception ex){
                System.out.println("  >> Client #" + Key_ThreadCount + " failed to initialize I/O Stream Objects. ");
            }

            // Send Client ID
            try{
                Send_Message(String.valueOf(Key_ThreadCount));
            }catch(Exception ex){
                System.out.println("Failed to send ID to the client host.");
            }

            while(!this.IsDonePlaying){
                while(true){
                    try{

                        Send_Message("Please select a category.");

                        Send_RemainingCategories(HT_NodeCategories);

                        PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount +"> selected the category, " + Receive_SelectedCategory( ));

                        PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Processing **" + this.Client_Selected_Category + "**");
                        WordMechanics wm = new WordMechanics(HT_NodeCategories.get(this.Client_Selected_Category).category.ListWords);
                        PRINT_CATEGORY_UNGUESSED_WORDS(wm);
                        wm.initializeWord( );
                        PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "The word the client must guess is **" + wm.SelectedWord + "**\n");

                        Send_GuessedLetters(wm.GuessedLetters);

                        Integer errorsMade = 0;
                        do{

                            Character letter =  Receive_Letter( );

                            if(wm.letterIsValid(letter)){
                                PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount,"<Client " + Key_ThreadCount +"> entered a valid letter: " + letter);
                            }
                            else if(wm.letterIsUsed(letter)){
                                PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount,"<Client " + Key_ThreadCount +"> entered an already used letter: " + letter);
                            }



                            if(!wm.SelectedWord.contains(letter.toString( )))
                                ++errorsMade;

                            Send_NumInvalidLettersEntered(errorsMade);

                            if(errorsMade.equals(6))
                                break;

                            wm.makeGuess(letter);

                            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, wm.get_GuessedLetters_String( ));

                            Send_GuessedLetters(wm.GuessedLetters);

                            Send_HasGuessedTheWord(wm.allLettersGuessed( ));
                        }while(!wm.allLettersGuessed( ));

                        if(errorsMade.equals(6)){
                            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount +"> failed to guess the word.");
                            HT_NodeCategories.get(this.Client_Selected_Category).numTriesLeft--;
                        }
                        else{
                            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount +"> has guessed the word for category, " + this.Client_Selected_Category);
                            HT_NodeCategories.get(this.Client_Selected_Category).categoryIsDone = true;
                        }

                        // Send game status
                            // -1 = Game is not over
                            // 0  = Game is over and client lost
                            // 1 = Game is over and client won.
                        if(this.EachCategoryHas_RemainingTries( ) && !this.EachCategoryHas_a_Word_Guessed( )){
                            Send_GameOverVal(-1);

                        }
                        else if(this.EachCategoryHas_a_Word_Guessed( )){
                            Send_GameOverVal(1);
                            Send_Message("Congratulations! You won!.");
                            break;
                        }
                        else{
                            Send_GameOverVal(0);
                            Send_Message("Game Over, you lost.");
                            break;
                        }



                    }catch(Exception ex){
                        try{
                            Accepted_Socket.close( );
                            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount + "> has left the server.");
                            SERVER_REPORT("<Client " + Key_ThreadCount + "> has left the server.");
                            HT_ClientThread.remove(this.Key_ThreadCount);

                            // Sets this specific Client Thread from the hashtable as no longer active.
                            HT_ClientThread.get(Key_ThreadCount).IsActive = false;

                        }catch(Exception e){
                            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Failed to close socket for <Client " + Key_ThreadCount + ">.");
                        }
                        break;
                    }
                } // while(true)

                // Prompt client to play again or not
                PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Prompting <Client " + Key_ThreadCount + ">. to play again or not.");
                try{
                    Send_Message("Play again?");
                }catch(Exception ex){
                    PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Failed to send [Play again] message to <Client " + Key_ThreadCount + ">.");
                }

                // Receive yes or no
                String message = Receive_Message( );
                if(message.equals("play again")){
                    PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount + ">. decided to play again.");
                    ResetCategories( );
                    // HT_NodeCategories.put(Def_Category01.Name, new NODE_CATEGORY(Def_Category01));
                    // HT_NodeCategories.put(Def_Category02.Name, new NODE_CATEGORY(Def_Category02));
                    // HT_NodeCategories.put(Def_Category03.Name, new NODE_CATEGORY(Def_Category03));

                }
                else if(message.equals("done")){
                    PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount + ">. decided not to play anymore.");
                    this.IsDonePlaying = true;
                }
            }


            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount + "> is finished.");
            try{
                PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "<Client " + Key_ThreadCount + "> has left the server.");
                SERVER_REPORT("<Client " + Key_ThreadCount + "> has left the server.");
                HT_ClientThread.remove(this.Key_ThreadCount);

                // Accepted_Socket.close( );
            }catch(Exception e){
                PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Failed to close socket for <Client " + Key_ThreadCount + ">.");
                SERVER_REPORT("<Client " + Key_ThreadCount + "> has left the server.");
            }

            // Closes the FileWriter ServerLog_txt class object.
            try{
                ServerLog_txt.close( );
            }catch(Exception ex){
                PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Failed to close the ServerLog text file creator.");
            }
        } // run

        //* HELPER METHODS
        public void ResetCategories( ){
            Set<String> setOfKeys = HT_NodeCategories.keySet( );
            for(String key: setOfKeys)
                HT_NodeCategories.get(key).categoryIsDone = false;
        }

        public Boolean EachCategoryHas_RemainingTries( ){
            Set<String> setOfKeys = HT_NodeCategories.keySet( );
            for(String key: setOfKeys){
                if(HT_NodeCategories.get(key).numTriesLeft.equals(0)){
                    return false;
                }
            }
            return true;
        }

        public Boolean EachCategoryHas_a_Word_Guessed( ){
            Set<String> setOfKeys = HT_NodeCategories.keySet( );
            for(String key: setOfKeys){
                if(HT_NodeCategories.get(key).categoryIsDone.equals(false)){
                    return false;
                }
            }
            return true;
        }

        public void SENDER_HELPER_FUNCTION(SerializedData sData, String exMsg) throws Exception{
            Output_Stream.writeObject(sData);
            Output_Stream.reset( );
        }

        public void  PRINT_CATEGORY_UNGUESSED_WORDS(WordMechanics wm){
            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Category, **" + this.Client_Selected_Category + "** un-guessed words");
            wm.getListOf_NotGuessedWords( ).forEach(e->PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, e));
        }

        public void ProcessSelectedCategory( ) throws Exception{

            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "Processing **" + this.Client_Selected_Category + "**");
            WordMechanics wm = new WordMechanics(HT_NodeCategories.get(this.Client_Selected_Category).category.ListWords);
            PRINT_CATEGORY_UNGUESSED_WORDS(wm);
            wm.initializeWord( );
            PRINT_AND_WRITE_CONSOLE_CLTHREAD(Key_ThreadCount, "The word the client must guess is **" + wm.SelectedWord + "**\n");
        }

        public String FORMATTED_SERVER_MESSAGE(String msg) {return " >> [SERVER]: " + msg + "\n";}

        //* SERIALIZED DATA SENDERS
        public void Send_Message(String msg) throws Exception{
            SerializedData sData = new SerializedData( );
            sData.Message = msg;
            this.SENDER_HELPER_FUNCTION(sData,
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.Message] to the client host."));
        }

        public void Send_SentCategoryIsValid(Boolean val) throws Exception{
            SerializedData sData = new SerializedData( );
            sData.SentCategoryIsValid = val;
            this.SENDER_HELPER_FUNCTION(sData,
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.SentCategoryIsValid] to the client host."));
        }

        public void Send_SelectedCategory(String category) throws Exception{
            this.SENDER_HELPER_FUNCTION(new SerializedData(category),
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.SelectedCategory] to client host."));
        }

        public void Send_Letter(Character letter) throws Exception{
            this.SENDER_HELPER_FUNCTION(new SerializedData(letter),
            FORMATTED_SERVER_MESSAGE("Failed to send [sDataLetter] to client."));
        }

        public void Send_GuessedLetters(List<Character> list) throws Exception{
            this.SENDER_HELPER_FUNCTION(new SerializedData(list),
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.GuessedLetters] to client."));
        }

        public void Send_HasGuessedTheWord(Boolean val) throws Exception{
            this.SENDER_HELPER_FUNCTION(new SerializedData(val),
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.HasGuessedTheWord] to cleint."));
        }

        public void Send_NumInvalidLettersEntered(Integer val) throws Exception{
            this.SENDER_HELPER_FUNCTION(new SerializedData(val),
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.NumInvalidLettersEntered] to cleint."));
        }

        public void Send_RemainingCategories(Hashtable<String, NODE_CATEGORY> ht) throws Exception{
            ArrayList<String> list = new ArrayList<String>( );

            Set<String> setOfKeys = ht.keySet( );
            for(String key: setOfKeys){
                if(!ht.get(key).categoryIsDone){
                    list.add(ht.get(key).category.Name);
                }
            }

            this.SENDER_HELPER_FUNCTION(new SerializedData(list),
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.RemainingCategories] to client."));
        }

        public void Send_GameOverVal(Integer val) throws Exception{
            SerializedData sData = new SerializedData( );
            sData.GameOverVal = val;
            this.SENDER_HELPER_FUNCTION(sData,
            FORMATTED_SERVER_MESSAGE("Failed to send [sData.GameOverVal] to the client host."));
        }

        //* SERIALIZED DATA RECEIVERS
        public String Receive_Message( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.Message;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.Message] from client host.");
                return null;
            }
        }

        public Boolean Receive_SentCategoryIsValid( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.SentCategoryIsValid;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.SentCategoryIsValid] from client host.");
                return null;
            }
        }

        public String Receive_SelectedCategory( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                this.Client_Selected_Category = sData.SelectedCategory; // Assigns the category to the ClientThread's data member
                return sData.SelectedCategory;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.SelectedCategory] from client host.");
                return null;
            }
        }

        public Character Receive_Letter( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.Letter;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.Letter] from client host.");
                return null;
            }
        }

        public List<Character> Receive_GuessedLetters( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.GuessedLetters;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.GuessedLetters] from client host.");
                return null;
            }
        }

        public Boolean Receive_HasGuessedTheWord( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.HasGuessedTheWord;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.HasGuessedTheWord] from client host.");
                return null;
            }
        }

        public Integer Receive_NumInvalidLettersEntered( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.NumInvalidLettersEntered;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.NumInvalidLettersEntered] from client host.");
                return null;
            }
        }

        public Integer Receive_GameOverVal( ){
            try{
                SerializedData sData = (SerializedData)Input_Stream.readObject( );
                return sData.GameOverVal;

            }catch(Exception ex){
                FORMATTED_SERVER_MESSAGE("Failed to receive [sData.GameOverVal] from client host.");
                return null;
            }
        }
    }
}