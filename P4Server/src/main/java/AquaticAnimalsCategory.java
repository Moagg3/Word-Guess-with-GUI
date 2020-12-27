public class AquaticAnimalsCategory extends Category{
    public AquaticAnimalsCategory( ){
        Name = "Aquatic";
        TotalWords = 5;

        ListWords.add(new Node("shark"   , false));
        ListWords.add(new Node("dolphin" , false));
        ListWords.add(new Node("octopus" , false));
        ListWords.add(new Node("starfish", false));
        ListWords.add(new Node("whale"   , false));
    }
}