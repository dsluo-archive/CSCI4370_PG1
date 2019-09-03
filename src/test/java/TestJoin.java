import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJoin {

    private Table movie;
    private Table studio;
    private Table equijoin;
    private Table natrualJoin;

    @Before
    public void setUp() {
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

        studio = new Table("studio", "name address presNo",
                "String String Integer", "name");
        var studio0 = new Comparable[]{"Fox", "Los_Angeles", 7777};
        var studio1 = new Comparable[]{"Universal", "Universal_City", 8888};
        var studio2 = new Comparable[]{"DreamWorks", "Universal_City", 9999};
        studio.insert(studio0);
        studio.insert(studio1);
        studio.insert(studio2);

        equijoin = new Table("equijoin", "title year length genre studioName producerNo name address presNo",
                "String Integer Integer String String Integer String String Integer", "title year");
    }

    @After
    public void tearDown() {
        movie = null;
        studio = null;
    }

    @Test
    public void nestedLoopJoin() {
    }

    @Test
    public void indexedJoin() {

    }

    @Test
    public void hashedJoin() {

    }

    @Test
    public void natrualJoin() {

    }
}