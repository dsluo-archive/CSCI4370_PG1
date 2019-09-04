import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestJoin {

    private Table movie;
    private Table studio;
    private Table equijoin;

    private Table student;
    private Table ta;
    private Table naturalJoin;

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
        var join0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345, "Fox", "Los_Angeles", 7777};
        var join1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345, "Fox", "Los_Angeles", 7777};
        var join2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125, "Universal", "Universal_City", 8888};
        var join3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355, "Universal", "Universal_City", 8888};
        equijoin.insert(join0);
        equijoin.insert(join1);
        equijoin.insert(join2);
        equijoin.insert(join3);

        student = new Table("student", "name id year gpa", "String Integer String Double", "id");
        var student0 = new Comparable[]{"Fred", 0, "Freshman", 3.5};
        var student1 = new Comparable[]{"Sally", 1, "Sophomore", 2.8};
        var student2 = new Comparable[]{"Susan", 2, "Graduate", 3.9};
        var student3 = new Comparable[]{"Robert", 3, "Senior", 3.1};
        student.insert(student0);
        student.insert(student1);
        student.insert(student2);
        student.insert(student3);

        ta = new Table("ta", "name id course", "String Integer String", "id");
        var ta0 = new Comparable[]{"Susan", 2, "Underwater Basket Weaving"};
        var ta1 = new Comparable[]{"Robert", 3, "Explosive Demolition 101"};
        ta.insert(ta0);
        ta.insert(ta1);

        naturalJoin = new Table("naturalJoin", "name id year gpa course",
                "String Integer Integer Float String", "id");
        var natJoin0 = new Comparable[]{"Susan", 2, "Graduate", 3.9, "Underwater Basket Weaving"};
        var natJoin1 = new Comparable[]{"Robert", 3, "Senior", 3.1, "Explosive Demolition 101"};
        naturalJoin.insert(natJoin0);
        naturalJoin.insert(natJoin1);

    }

    @After
    public void tearDown() {
        movie = null;
        studio = null;
    }

    @Test
    public void nestedLoopJoin() {
        var joined = movie.join("studioName", "name", studio);
        assertTrue(equijoin.equalsIgnoreName(joined));
    }

//    @Test
//    public void indexedJoin() {
//        var joined = movie.i_join("studioName", "name", studio);
//        assertTrue(equijoin.equalsIgnoreName(joined));
//    }

    @Test
    public void hashedJoin() {
        var joined = movie.h_join("studioName", "name", studio);
        assertTrue(equijoin.equalsIgnoreName(joined));
    }

    @Test
    public void naturalJoin() {
        var joined = student.join(ta);
        assertTrue(naturalJoin.equalsIgnoreName(joined));
    }
}
