import ServerCode.*;
import javafx.application.Application;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GuessTest extends Category{



    @Test
    void test1( ){

        ArrayList<String> list = new ArrayList<String>();
        list.add("apple");
        list.add("towel");
        list.add("shark");

        WordMechanics wm = new WordMechanics("shark", list);
        wm.SelectedWord = "shark";
        assertEquals("shark", wm.SelectedWord);
    }

    @Test
    void test2( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bad");
        list.add("sad");
        list.add("lad");

        WordMechanics wm = new WordMechanics("bad", list);

        wm.makeGuess('a');
        assertEquals(2, wm.TotalLettersRemaining);
    }

    @Test
    void test3( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bad");
        list.add("sad");
        list.add("lad");

        WordMechanics wm = new WordMechanics("bad", list);

        wm.makeGuess('a');
        assertEquals("_ A _ ", wm.get_GuessedLetters_String());
    }

    @Test
    void test4( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bad");
        list.add("sad");
        list.add("lad");

        WordMechanics wm = new WordMechanics("bad", list);
        assertTrue(wm.letterIsValid('a'));
    }

    @Test
    void test5( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bad");
        list.add("sad");
        list.add("lad");

        WordMechanics wm = new WordMechanics("bad", list);
        wm.makeGuess('d');
        assertTrue(wm.letterIsUsed('d'));
    }

    @Test
    void test6( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bad");
        list.add("sad");
        list.add("lad");

        WordMechanics wm = new WordMechanics("bad", list);

        assertEquals(3, wm.ListWordStr.size());
    }

    @Test
    void test7( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("blessed");
        WordMechanics wm = new WordMechanics("blessed", list);

        assertEquals("_ _ _ _ _ _ _ ", wm.get_GuessedLetters_String());
    }

    @Test
    void test8( ){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bad");
        list.add("sad");
        list.add("lad");

        WordMechanics wm = new WordMechanics("bad", list);

        assertEquals(3, wm.SelectedWord.length());
    }

    @Test
    void test9( ){
        Category Def_Category01;
        Category Def_Category02;
        Category Def_Category03;

        WordCategoryGenerator animalCategory = new AnimalCategoryGenerator( );
        WordCategoryGenerator peopleCategory = new PeopleCategoryGenerator( );
        WordCategoryGenerator foodCategory   = new FoodCategoryGenerator( );
        Def_Category01 = animalCategory.createCategory("Aquatic"); // Aquatic Animals
        Def_Category02 = peopleCategory.createCategory("USPresidents");
        Def_Category03 = foodCategory.createCategory("Fruits");

        assertEquals(5, Def_Category01.getList().size());

    }

    @Test
    void test10( ){
        Category Def_Category01;
        Category Def_Category02;
        Category Def_Category03;

        WordCategoryGenerator animalCategory = new AnimalCategoryGenerator( );
        WordCategoryGenerator peopleCategory = new PeopleCategoryGenerator( );
        WordCategoryGenerator foodCategory   = new FoodCategoryGenerator( );
        Def_Category01 = animalCategory.createCategory("Aquatic"); // Aquatic Animals
        Def_Category02 = peopleCategory.createCategory("USPresidents");
        Def_Category03 = foodCategory.createCategory("Fruits");


        assertEquals("Fruits", Def_Category03.getCategoryName());


    }

    @Test
    void test11( ){
        Category Def_Category01;
        Category Def_Category02;
        Category Def_Category03;

        WordCategoryGenerator animalCategory = new AnimalCategoryGenerator( );
        WordCategoryGenerator peopleCategory = new PeopleCategoryGenerator( );
        WordCategoryGenerator foodCategory   = new FoodCategoryGenerator( );
        Def_Category01 = animalCategory.createCategory("Aquatic"); // Aquatic Animals
        Def_Category02 = peopleCategory.createCategory("USPresidents");
        Def_Category03 = foodCategory.createCategory("Fruits");


        assertEquals("Aquatic", Def_Category01.getCategoryName());


    }

}
