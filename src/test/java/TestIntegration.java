/*****************************************************************************************
 * @file TestIntegration.java
 *
 * @author Devan Vitha
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/*****************************************************************************************
 * The TestProjectSelect class tests the project and select methods.
 */
public class TestIntegration {

    private Table movie;
    private Table Actor;
    private Table key1;
    

    /************************************************************************************
     * Sets up all the tables before testings
     */
    @Before
    public void setUp() {
        movie = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title");
        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};
        //var film4 = new Comparable[]{"Infinity Wars", 1988, 130, "action", "Fox", 12346};
        var film5 = new Comparable[]{"Avengers", 2000, 160, "action", "Fox", 12346};
        var film6 = new Comparable[]{"Terminator", 1985, 300, "action", "Universal", 12111};
        var film7 = new Comparable[]{"Zohan", 1978, 100, "Comedy", "Universal", 42355};
        var film8 = new Comparable[]{"launch", 1977, 124, "sciFi", "Fox", 12345};
        var film9 = new Comparable[]{"The Belko Experiment", 2011, 120, "action", "Fox", 12355};


        movie.insert(film0);
        movie.insert(film1);
        movie.insert(film2);
        movie.insert(film3);
        movie.insert(film5);
        movie.insert(film6);
        movie.insert(film7);
        movie.insert(film8);
        movie.insert(film9);


        Actor = new Table("Actor", "name movieTitle address gender birthdate",
                "String String String Character String", "name");
        var star0 = new Comparable[]{"Carrie_Fisher", "Rocky", "Hollywood", 'F', "9/9/99"};
        var star1 = new Comparable[]{"Mark_Hamill", "Zohan", "Brentwood", 'M', "8/8/88"};
        var star2 = new Comparable[]{"Harrison_Ford", "Terminator", "Beverly_Hills", 'M', "7/7/77"};

        Actor.insert(star0);
        Actor.insert(star1);
        Actor.insert(star2);

        key1 = new Table("key", "title year genre name movieTitle address gender birthdate",
                "String Integer String String String String Character String", "title");
        var Key0Tuple = new Comparable[]{"Zohan", 1978, "Comedy","Mark_Hamill","Zohan","Brentwood",'M',"8/8/88"};

        key1.insert(Key0Tuple);

    }

    /************************************************************************************
     * Clears all the tables after testing
     */
    @After
    public void tearDown() {
        movie = null;
        Actor = null;
        key1 = null;
    }


    /************************************************************************************
     * Tests all methods in a specfic order
     */
    @Test
    public void IntegrationTest1(){
        var movieproject = (movie.project("title year genre"));
        var selectActor1 = Actor.select(new KeyType("Carrie_Fisher"));
        var selectActor2 = Actor.select(new KeyType("Harrison_Ford"));
        var selectActor3 = Actor.select(new KeyType("Mark_Hamill"));
        var unionSelectedActors1and2 = selectActor1.union(selectActor2);
        var unionSelectedActors = unionSelectedActors1and2.union(selectActor3);

        var shouldBeActor1and2 = unionSelectedActors.minus(selectActor3);

        var joinOnTitle = movieproject.join("title","movieTitle", shouldBeActor1and2);
        var joinOnTitle2 = movieproject.h_join("title","movieTitle", unionSelectedActors);
        var finalTable = joinOnTitle2.minus(joinOnTitle);






        assertTrue(finalTable.equalsIgnoreName(key1));
    }

}
