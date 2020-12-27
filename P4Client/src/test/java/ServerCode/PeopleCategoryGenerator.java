package ServerCode;

public class PeopleCategoryGenerator extends WordCategoryGenerator{
    public Category createCategory(String type){
        if(type.equals("USPresidents")){
            return new USPresidentsCategory( );
        }
        else return null;
    }
}