
public class FoodCategoryGenerator extends WordCategoryGenerator{
    @Override
    protected Category createCategory(String type){
        if(type.equals("Fruits"))
            return new FruitsCategory( );
        else return null;
    }
}