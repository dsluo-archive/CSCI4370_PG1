import org.junit.*;
import static org.junit.Assert.*;

public class TestProjectSelect {

    private Table movie;
    private Table movieStar;

    private Table projectOldKey;
    private Table projectNewKey;
    private Table select;

    @Before
    public void setUp(){
        movie = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");
        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};
        movie.insert(film0);
        movie.insert(film1);
        movie.insert(film2);
        movie.insert(film3);

        movieStar = new Table("movieStar", "name address gender birthdate",
                "String String Character String", "name");
        var star0 = new Comparable[]{"Carrie_Fisher", "Hollywood", 'F', "9/9/99"};
        var star1 = new Comparable[]{"Mark_Hamill", "Brentwood", 'M', "8/8/88"};
        var star2 = new Comparable[]{"Harrison_Ford", "Beverly_Hills", 'M', "7/7/77"};
        movieStar.insert(star0);
        movieStar.insert(star1);
        movieStar.insert(star2);

        projectOldKey = new Table("movie", "title year genre",
                "String Integer String ", "title year");
        var projectOldKey0 = new Comparable[]{"Star_Wars", 1977, "sciFi"};
        var projectOldKey1 = new Comparable[]{"Star_Wars_2", 1980, "sciFi"};
        var projectOldKey2 = new Comparable[]{"Rocky", 1985, "action"};
        var projectOldKey3 = new Comparable[]{"Rambo", 1978, "action"};
        projectOldKey.insert(projectOldKey0);
        projectOldKey.insert(projectOldKey1);
        projectOldKey.insert(projectOldKey2);
        projectOldKey.insert(projectOldKey3);

        projectNewKey = new Table("movie", "year length genre studioName",
                "Integer Integer String String", "year length genre studioName");
        var projectNewKey0 = new Comparable[]{1977, 124, "sciFi", "Fox"};
        var projectNewKey1 = new Comparable[]{1980, 124, "sciFi", "Fox"};
        var projectNewKey2 = new Comparable[]{1985, 200, "action", "Universal"};
        var projectNewKey3 = new Comparable[]{1978, 100, "action", "Universal"};
        projectNewKey.insert(projectNewKey0);
        projectNewKey.insert(projectNewKey1);
        projectNewKey.insert(projectNewKey2);
        projectNewKey.insert(projectNewKey3);

        select = new Table("movieStar", "name address gender birthdate",
                "String String Character String", "name");
        var select0 = new Comparable[]{"Carrie_Fisher", "Hollywood", 'F', "9/9/99"};
        select.insert(select0);
    }

    @After
    public void tearDown(){
        movie = null;
        movieStar = null;
    }

    @Test
    public void projecttest1(){
        var movieproject = movie.project("title year genre");

        assertTrue(projectOldKey.equalsIgnoreName(movieproject));
    }

    @Test
    public void projecttest2(){
        var movieproject = movie.project("year length genre studioName");

        assertTrue(projectNewKey.equalsIgnoreName(movieproject));
    }

    @Test
    public void selecttest(){
        var movieStarSelect = movieStar.select(new KeyType("Carrie_Fisher"));

        assertTrue(select.equalsIgnoreName(movieStarSelect));
    }
}
