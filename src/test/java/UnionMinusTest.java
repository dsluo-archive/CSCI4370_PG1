import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class UnionMinusTest {

    /************************************************************************************
     * Tests to make sure that performing union on union incompatible tables returns null
     * and that the union of two compatible tables returns a table with both of the
     * tuples
     * Uses reflection to access tuples
     *
     * @return void
     * @author Ravi Parashar
     */
    @org.junit.Test
    public void union() {
        var movie = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var cinema = new Table("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var movieStar = new Table("movieStar", "name address gender birthdate",
                "String String Character String", "name");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};
        var film4 = new Comparable[]{"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};
        movie.insert(film0);
        movie.insert(film1);
        movie.insert(film2);
        movie.insert(film3);
        cinema.insert(film2);
        cinema.insert(film3);
        cinema.insert(film4);

        var star0 = new Comparable[]{"Carrie_Fisher", "Hollywood", 'F', "9/9/99"};
        var star1 = new Comparable[]{"Mark_Hamill", "Brentwood", 'M', "8/8/88"};
        var star2 = new Comparable[]{"Harrison_Ford", "Beverly_Hills", 'M', "7/7/77"};
        movieStar.insert(star0);
        movieStar.insert(star1);
        movieStar.insert(star2);

        //should return film0, film1, film2, film3, film2, film3, and film4
        var union1 = movie.union(cinema);
        //incompatible
        var union2 = movie.union(movieStar);

        var truth1 = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");
        truth1.insert(film0);
        truth1.insert(film1);
        truth1.insert(film2);
        truth1.insert(film3);
        truth1.insert(film2);
        truth1.insert(film3);
        truth1.insert(film4);
        Field uniontuple = null;
        Field truthtuple = null;
        try {
            uniontuple = union1.getClass().getDeclaredField("tuples");
            uniontuple.setAccessible(true);
            uniontuple.get(union1);
        }
        catch(Exception e){
            System.out.println("Trouble accessing.");
        }
        try {
            truthtuple = truth1.getClass().getDeclaredField("tuples");
            truthtuple.setAccessible(true);
            truthtuple.get(truth1);
        }
        catch(Exception e){
            System.out.println("Trouble accessing.");
        }

        assertEquals(uniontuple, truthtuple);
        assertEquals(null, union2);
    }

    /************************************************************************************
     * Tests to make sure that performing minus on union incompatible tables returns null
     * and that one table minus a compatible one returns the tuples in the first one that
     * are not present in the second one.
     * Uses reflection to access tuples
     *
     * @return void
     * @author Ravi Parashar
     */
    @org.junit.Test
    public void minus() {
        var movie = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var cinema = new Table("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var movieStar = new Table("movieStar", "name address gender birthdate",
                "String String Character String", "name");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};
        var film4 = new Comparable[]{"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};
        movie.insert(film0);
        movie.insert(film1);
        movie.insert(film2);
        movie.insert(film3);
        cinema.insert(film2);
        cinema.insert(film3);
        cinema.insert(film4);

        var star0 = new Comparable[]{"Carrie_Fisher", "Hollywood", 'F', "9/9/99"};
        var star1 = new Comparable[]{"Mark_Hamill", "Brentwood", 'M', "8/8/88"};
        var star2 = new Comparable[]{"Harrison_Ford", "Beverly_Hills", 'M', "7/7/77"};
        movieStar.insert(star0);
        movieStar.insert(star1);
        movieStar.insert(star2);

        //should return film0 and film1
        var minus1 = movie.minus(cinema);
        //incompatible
        var minus2 = movie.minus(movieStar);

        var truth1 = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");
        truth1.insert(film0);
        truth1.insert(film1);
        Field minustuple = null;
        Field truthtuple = null;
        try {
            minustuple = minus1.getClass().getDeclaredField("tuples");
            minustuple.setAccessible(true);
            minustuple.get(minus1);
        }
        catch(Exception e){
            System.out.println("Trouble accessing.");
        }
        try {
            truthtuple = truth1.getClass().getDeclaredField("tuples");
            truthtuple.setAccessible(true);
            truthtuple.get(truth1);
        }
        catch(Exception e){
            System.out.println("Trouble accessing.");
        }

        assertEquals(minustuple, truthtuple);
        assertEquals(null, minus2);
    }
}