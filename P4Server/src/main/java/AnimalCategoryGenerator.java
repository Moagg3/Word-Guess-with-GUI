
public class AnimalCategoryGenerator extends WordCategoryGenerator{
    protected Category createCategory(String type){
        if(type.equals("Aquatic")){
            return new AquaticAnimalsCategory( );
        }
        else return null;
    }
}