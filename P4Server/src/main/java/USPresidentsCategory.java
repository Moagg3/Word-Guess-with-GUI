
public class USPresidentsCategory extends Category{
    public USPresidentsCategory( ){
        Name = "USPresidents";
        TotalWords = 5;

        ListWords.add(new Node("Lincoln", false));
        ListWords.add(new Node("Trump"  , false));
        ListWords.add(new Node("Reagan" , false));
        ListWords.add(new Node("Nixon"  , false));
        ListWords.add(new Node("Kennedy", false));
    }
}