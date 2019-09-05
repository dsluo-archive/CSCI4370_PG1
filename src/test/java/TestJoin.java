import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit Tests
 *
 * @author David Luo
 */
public class TestJoin {
    private Table student;
    private Table ta;

    private Table equiJoin;
    private Table naturalJoin;

    /**
     * Set up testing fixtures.
     * <p>
     * `student` and `ta` are inputs
     * `equiJion` and `naturalJoin` are expected outputs.
     */
    @Before
    public void setUp() {
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

        equiJoin = new Table("equijoin", "name id year gpa name2 id2 course",
                "String Integer String Double String Integer String", "id");
        var eqJoin0 = new Comparable[]{"Susan", 2, "Graduate", 3.9, "Susan", 2, "Underwater Basket Weaving"};
        var eqJoin1 = new Comparable[]{"Robert", 3, "Senior", 3.1, "Robert", 3, "Explosive Demolition 101"};
        equiJoin.insert(eqJoin0);
        equiJoin.insert(eqJoin1);

        naturalJoin = new Table("naturalJoin", "name id year gpa course",
                "String Integer String Double String", "id");
        var natJoin0 = new Comparable[]{"Susan", 2, "Graduate", 3.9, "Underwater Basket Weaving"};
        var natJoin1 = new Comparable[]{"Robert", 3, "Senior", 3.1, "Explosive Demolition 101"};
        naturalJoin.insert(natJoin0);
        naturalJoin.insert(natJoin1);
    }

    @After
    public void tearDown() {
        student = null;
        ta = null;
        equiJoin = null;
        naturalJoin = null;
    }

    /**
     * Test nested loop join. Tests `student` ⋈_x `ta` where `x` is the following:
     * - x = student.name = ta.name
     * - x = student.id = ta.id
     * - x = student.name = ta.name && student.id = ta.id
     */
    @Test
    public void nestedLoopJoin() {
        var nameJoin = student.join("name", "name", ta);
        assertTrue(equiJoin.equalsIgnoreName(nameJoin));
        var idJoin = student.join("id", "id", ta);
        assertTrue(equiJoin.equalsIgnoreName(idJoin));
        var nameIdJoin = student.join("name id", "name id", ta);
        assertTrue(equiJoin.equalsIgnoreName(nameIdJoin));
    }

    /**
     * Test hashed join. Tests `student` ⋈_x `ta` where `x` is the following:
     * - x = student.name = ta.name
     * - x = student.id = ta.id
     * - x = student.name = ta.name && student.id = ta.id
     */
    @Test
    public void hashedJoin() {
        var nameJoin = student.h_join("name", "name", ta);
        assertTrue(equiJoin.equalsIgnoreName(nameJoin));
        var idJoin = student.h_join("id", "id", ta);
        assertTrue(equiJoin.equalsIgnoreName(idJoin));
        var nameIdJoin = student.h_join("name id", "name id", ta);
        assertTrue(equiJoin.equalsIgnoreName(nameIdJoin));
    }

    /**
     * Tests natural join. Equivalent to `student` ⋈_x `ta` where `x` = student.name = ta.name && student.id = ta.id
     */
    @Test
    public void naturalJoin() {
        var joined = student.join(ta);
        assertTrue(naturalJoin.equalsIgnoreName(joined));
    }
}