package ServerCode;

public class AnimalCategoryGenerator extends WordCategoryGenerator{
    public Category createCategory(String type){
        if(type.equals("Aquatic")){
            return new AquaticAnimalsCategory( );
        }
        else return null;
    }
}