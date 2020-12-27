package ServerCode;

public class FoodCategoryGenerator extends WordCategoryGenerator{
    @Override
    public Category createCategory(String type){
        if(type.equals("Fruits"))
            return new FruitsCategory( );
        else return null;
    }
}