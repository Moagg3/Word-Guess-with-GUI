import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializedData implements Serializable{
    /**
     * All data members must also be serializable
     */
    private static final long serialVersionUID = 1L;

    String Message;
    Boolean SentCategoryIsValid = false;
    
    // Parametirezed Data Members
    String SelectedCategory;
    Character Letter;
    List<Character> GuessedLetters;
    Boolean HasGuessedTheWord;
    Integer NumInvalidLettersEntered;

    ArrayList<String> RemainingCategories;

    Integer GameOverVal;
    // -1 = Game is not over
    // 0  = Game is over and client lost
    // 1 = Game is over and client won.

    SerializedData( ){
        super( );
    }

    public SerializedData(String _SelectedCategory){
        this.SelectedCategory = _SelectedCategory;
    }

    public SerializedData(Character _Letter){
        this.Letter = _Letter;
    }

    public SerializedData(List<Character> _GuessedLetters){
        this.GuessedLetters = _GuessedLetters;
    }

    public SerializedData(Boolean _HasGuessedTheWord){
        this.HasGuessedTheWord = _HasGuessedTheWord;
        System.out.println("In serialized data: " + this.HasGuessedTheWord);
    }

    public SerializedData(Integer _NumInvalidLettersEntered){
        this.NumInvalidLettersEntered = _NumInvalidLettersEntered;
    }

    public SerializedData(ArrayList<String> _RemainingCategories){
        this.RemainingCategories = _RemainingCategories;
    }
}