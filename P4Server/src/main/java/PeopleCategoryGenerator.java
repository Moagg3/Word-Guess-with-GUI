
public class PeopleCategoryGenerator extends WordCategoryGenerator{
    protected Category createCategory(String type){
        if(type.equals("USPresidents")){
            return new USPresidentsCategory( );
        }
        else return null;
    }
}