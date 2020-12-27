import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WordMechanics extends Category{
    ArrayList<Node> ListWords = null;
    String SelectedWord;
    List<Character> GuessedLetters = new ArrayList<Character>();
    Integer TotalLettersRemaining;
    Boolean WordIsGuessed;
    Integer CurrentNodeIndex;
    ArrayList<Character> ValidLettersUsed = new ArrayList<Character>();

    WordMechanics(ArrayList<Node> list){
        ListWords   = list;
        CurrentNodeIndex = -1;
    }

    void initializeWord( ){
        SelectedWord          = this.getWordNode( ).word;
        TotalLettersRemaining = SelectedWord.length( );
        WordIsGuessed         = false;

        for(int i = 0; i < SelectedWord.length(); ++i){
            GuessedLetters.add('_');
        }
    }

    String get_GuessedLetters_String( ){
        StringBuilder str = new StringBuilder();
        for (Character character : GuessedLetters) {
            str.append(character + " ");
        }
        return str.toString( );
    }

    Boolean allLettersGuessed( ){
        if(TotalLettersRemaining.equals(0)){
            // GuessedLetters = new ArrayList<Character>( );
            return true;
        }
        return false;
    }

    // Updates the blank lines by replacing them with the correct guessed letter
    // Updates the value of the TotalLetterRemaining data member
    // Updates which word is completely guessed correctly
    void makeGuess(Character letter){
        assert TotalLettersRemaining > 0;

        String letter_str = letter.toString( );
        letter_str = letter_str.toUpperCase();

        Integer index = 0;
        while(!index.equals(SelectedWord.length())){
            if(this.letterIsUsed(letter)){
                System.out.println("LETTER WAS ALREADY USED BEFORE");
                break;
            }
            
            // Coversion from primitive char data -> wrapper class Character -> wrapper class String
            String letter_atIndex = String.valueOf(SelectedWord.charAt(index));

            // if(letter.equals(SelectedWord.charAt(index)))
            if(letter_str.equalsIgnoreCase(letter_atIndex)){
                GuessedLetters.set(index, letter_str.charAt(0));
                --TotalLettersRemaining;
                if(TotalLettersRemaining.equals(0)){
                    WordIsGuessed = true;
                    ListWords.get(CurrentNodeIndex).isGuessed = true;
                }
            }
            ++index;
        }
        this.ValidLettersUsed.add(letter);
    }

    // Returns true if the letter argument is found in the SelectedWord string
    Boolean letterIsValid(Character letter){
        if(SelectedWord.contains(letter.toString())  && !letterIsUsed(letter)){
            return true;
        }
        return false;
    }

    Boolean letterIsUsed(Character letter){
        for (Character character : ValidLettersUsed) {
            if(character.equals(letter)) 
                return true;
        }
        return false;
    }

    // Attains a Node that contains a word at random
    Node getWordNode( ){
        assert !ListWords.isEmpty( ) : "In class WordMechanics, no ListWords was passed";
        Integer min   = 0;
        Integer max   = 4;
        int randomVal = ThreadLocalRandom.current().nextInt(min, max + 1);

        if(this.allWordsGuessed()){
            System.out.println("All words are guessed");
            return null;
        }
        if(ListWords.get(randomVal).isGuessed.equals(false)){
            CurrentNodeIndex = randomVal;
            return ListWords.get(randomVal);
        }
        else{
            while(ListWords.get(randomVal).isGuessed.equals(true)){
                System.out.println("***");
                if(ListWords.get(randomVal).isGuessed.equals(false))
                    break;
                randomVal = ThreadLocalRandom.current().nextInt(min, max + 1);
            }
        }
        CurrentNodeIndex = randomVal;
        return ListWords.get(randomVal);
    }

    // Returns true if all words in the list are guessed correctly
    Boolean allWordsGuessed( ){
        for(int i = 0; i < ListWords.size(); ++i){
            if(ListWords.get(i).isGuessed.equals(false))
                return false;
        }
        return true;
    }

    void printGuessedLetters( ){
        GuessedLetters.forEach(e->System.out.print(e + " "));
        System.out.println( );
    }

    void printList( ){
        for (Node node : ListWords) {
            int maxSpace = 10;
            int desiredSpace = maxSpace - node.word.length();
            System.out.print("Word: [" + node.word + "] ");
            for(int i = 0; i < desiredSpace; ++i)
                System.out.print("-");
            System.out.print("isGuessed: [" + node.isGuessed + "] \n");
        }
        System.out.println( );
    }

    ArrayList<String> getListOf_NotGuessedWords( ){
        ArrayList<String> list = new ArrayList<String>( );
        for (Node node : ListWords) {
            if(node.isGuessed.equals(false)){
                list.add(node.word);
            }
        }
        return list;
    }

    ArrayList<String> getListOf_GuessedWords( ){
        ArrayList<String> list = new ArrayList<String>( );
        for (Node node : ListWords) {
            if(node.isGuessed.equals(true)){
                list.add(node.word);
            }
        }
        return list;
    }
}