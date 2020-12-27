
public abstract class WordCategoryGenerator{
    public Category GetCategory(String type){
        Category category;
        category = createCategory(type);
        return category;
    }

    protected abstract Category createCategory(String type);
}