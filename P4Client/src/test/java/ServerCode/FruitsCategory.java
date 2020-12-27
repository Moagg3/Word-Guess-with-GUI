package ServerCode;

public class FruitsCategory extends Category{
    public FruitsCategory( ){
        Name = "Fruits";
        TotalWords = 5;

        ListWords.add(new Node("apple"     , false));
        ListWords.add(new Node("banana"    , false));
        ListWords.add(new Node("cantaloupe", false));
        ListWords.add(new Node("date"      , false));
        ListWords.add(new Node("evergreen" , false));
    }
}