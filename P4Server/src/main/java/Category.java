import java.util.ArrayList;

public abstract class Category {
    protected class Node{
        String word;
        Boolean isGuessed;

        Node(String _word, Boolean val){
            this.word      = _word;
            this.isGuessed = val;
        }
    }

    String  Name;
    Integer TotalWords;
    ArrayList<Node> ListWords = new ArrayList<Node>( );

    public String getCategoryName( ) {return Name;}
    public ArrayList<Node> getList( ) {return ListWords;}

    public void setWordisGuessed(Integer index, Boolean val){
        if(ListWords.isEmpty( ))
            return;
        
        ListWords.get(index).isGuessed = val;
    }

    public void setWordisGuessed(String _word, Boolean val){
        // if(ListWords.isEmpty( ))
        //     return;
        
        assert(!ListWords.isEmpty());
        for (Node node : ListWords) {
            if(node.word.equals(_word)){
                node.isGuessed = val;
            }
        }
    }

    public Boolean isGuessed(String _word){
        if(this.getNode(_word).isGuessed)
            return true;
        return false;
    }

    public String getWord(Integer index){
        assert(!ListWords.isEmpty());
        return ListWords.get(index).word;
    }

    public Node getNode(String _word){
        assert(!ListWords.isEmpty());
        for (Node node : ListWords) {
            if(node.word.equals(_word)){
                return node;
            }
        }
        return null; // Word not found in the list
    }

    public void printListInfo( ){
        assert(!ListWords.isEmpty());
        System.out.println("  ** PRINTING LIST CATEGORY INFO ** ");
        System.out.println("    >> 1. Category Name: " + Name);
        System.out.println("    >> 2. Total Words: " + ListWords.size( ));
        System.out.println("    >> 3. List Contents: ");
        for (Node node : ListWords) {
            System.out.println("        Word: " + node.word + ", isGuessed: " + node.isGuessed);
        }
    }

}