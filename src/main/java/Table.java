
/****************************************************************************************
 * @file Table.java
 *
 * @author  David Luo
 * @author  Ravi Parashar
 * @author  Miruna Cristian
 * @author  Devan Vitha
 */

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.System.out;

/****************************************************************************************
 * This class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join.  The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
        implements Serializable {
    /**
     * Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /**
     * Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /**
     * Counter for naming temporary tables.
     */
    private static int count = 0;

    /**
     * Table name.
     */
    private final String name;

    /**
     * Array of attribute names.
     */
    private final String[] attribute;

    /**
     * Array of attribute domains: a domain may be
     * integer types: Long, Integer, Short, Byte
     * real types: Double, Float
     * string types: Character, String
     */
    private final Class[] domain;

    /**
     * Collection of tuples (data storage).
     */
    private final List<Comparable[]> tuples;

    /**
     * Primary key.
     */
    private final String[] key;

    /**
     * Index into tuples (maps key to tuple number).
     */
    private final Map<KeyType, Comparable[]> index;

    /**
     * The supported map types.
     */
    private enum MapType {NO_MAP, TREE_MAP, LINHASH_MAP, BPTREE_MAP}

    /**
     * The map type to be used for indices.  Change as needed.
     */
    private static final MapType mType = MapType.TREE_MAP;

    /************************************************************************************
     * Make a map (index) given the MapType.
     */
    private static Map<KeyType, Comparable[]> makeMap() {
        switch (mType) {
            case TREE_MAP:
                return new TreeMap<>();
//        case LINHASH_MAP -> new LinHashMap <> (KeyType.class, Comparable [].class);
//        case BPTREE_MAP  -> new BpTreeMap <> (KeyType.class, Comparable [].class);
            default:
                return null;
        }
        // switch
    } // makeMap

    //-----------------------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------------------

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key) {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = new ArrayList<>();
        index = makeMap();

    } // primary constructor

    /************************************************************************************
     * Construct a table from the meta-data specifications and data in _tuples list.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples     the list of tuples containing the data
     */
    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key,
                 List<Comparable[]> _tuples) {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = _tuples;
        index = makeMap();
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param _name       the name of the relation
     * @param attributes  the string containing attributes names
     * @param domains     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table(String _name, String attributes, String domains, String _key) {
        this(_name, attributes.split(" "), findClass(domains.split(" ")), _key.split(" "));

        out.println("DDL> create table " + name + " (" + attributes + ")");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     *
     * #usage movie.project ("title year studioNo")
     *
     * @param attributes  the attributes to project onto
     * @return a table of projected tuples
     */
    public Table project(String attributes) {
        out.println("RA> " + name + ".project (" + attributes + ")");
        var attrs = attributes.split(" ");
        var colDomain = extractDom(match(attrs), domain);
        var newKey = (Arrays.asList(attrs).containsAll(Arrays.asList(key))) ? key : attrs;

        List<Comparable[]> rows = new ArrayList<>();

        //Go through all the tuples of table
        for(int i = 0; i < tuples.size(); i++) {
            //Get only the columns that are needed
            var t = this.extract(this.tuples.get(i), attrs);
            //Insert the tuple to the list
            rows.add(t);
        }

        return new Table(name + count++, attrs, colDomain, newKey, rows);
    } // project

    /************************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     *
     * #usage movie.select (t -> t[movie.col("year")].equals (1977))
     *
     * @param predicate  the check condition for tuples
     * @return a table with tuples satisfying the predicate
     */
    public Table select(Predicate<Comparable[]> predicate) {
        out.println("RA> " + name + ".select (" + predicate + ")");

        return new Table(name + count++, attribute, domain, key,
                tuples.stream().filter(t -> predicate.test(t))
                        .collect(Collectors.toList()));
    } // select

    /************************************************************************************
     * Select the tuples satisfying the given key predicate (key = value).  Use an index
     * (Map) to retrieve the tuple with the given key value.
     * @author Miruna Cristian
     * @param keyVal  the given key value
     * @return a table with the tuple satisfying the key predicate
     */
    public Table select(KeyType keyVal) {
        out.println("RA> " + name + ".select (" + keyVal + ")");

        List<Comparable[]> rows = new ArrayList<>();

        //Search the tuples to find the ones that match the key
        for(int i = 0; i < tuples.size(); i++) {
            //get an individual tuple
            var t = this.extract(this.tuples.get(i), this.key);
            //turn it in a KeyType
            KeyType comparison = new KeyType(new Comparable[] {t[0]});

            //compare the keyVal to the tuple
            if(keyVal.compareTo(comparison) == 0){
                //if true add to the new table
                rows.add(this.tuples.get(i));
            }
        }

        return new Table(name + count++, attribute, domain, key, rows);
    } // select

    /************************************************************************************
     * Union this table and table2.  Check that the two tables are compatible. Puts all
     * tuples in this table and table2 into one table, not checking for duplicates.
     *
     * #usage movie.union (show)
     *
     * @param table2  the rhs table in the union operation
     * @return a table representing the union or null if the tables are union incompatible
     * @author Ravi Parashar
     */
    public Table union(Table table2) {
        out.println("RA> " + name + ".union (" + table2.name + ")");
        if (!compatible(table2)) return null;

        List<Comparable[]> rows = new ArrayList<>();

        Table table1 = this;
        for (Comparable[] x: table1.tuples) {
            rows.add(x);
        }
        for (Comparable[] x: table2.tuples) {
            rows.add(x);
        }

        return new Table(name + count++, attribute, domain, key, rows);
    } // union

    /************************************************************************************
     * Take the difference of this table and table2.  Check that the two tables are
     * compatible. Puts all tuples in this table but not in table2 into one table.
     *
     * #usage movie.minus (show)
     *
     * @param table2  The rhs table in the minus operation
     * @return a table representing the difference or null if the tables are union incompatible
     * @author Ravi Parashar
     */
    public Table minus(Table table2) {
        out.println("RA> " + name + ".minus (" + table2.name + ")");
        if (!compatible(table2)) return null;

        List<Comparable[]> rows = new ArrayList<>();
        boolean brk = false;
        Table table1 = this;
        for (Comparable[] x1: table1.tuples) {
            for(Comparable[] x2: table2.tuples){
                if(Arrays.equals(x1, x2)){
                    brk = true;
                    break;
                } else {
                    brk = false;
                }
            }
            if(!brk){
                rows.add(x1);
            }
        }

        return new Table(name + count++, attribute, domain, key, rows);
    } // minus

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by append "2" to the end of any duplicate attribute name.  Implement using
     * a Nested Loop Join algorithm.
     *
     * @author David Luo
     *
     * #usage movie.join ("studioNo", "name", studio)
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     */
    public Table join(String attributes1, String attributes2, Table table2) {
        out.println("RA> " + name + ".join (" + attributes1 + ", " + attributes2 + ", " + table2.name + ")");

        var t_attrs = attributes1.split(" ");
        var u_attrs = attributes2.split(" ");

        if (t_attrs.length != u_attrs.length)
            throw new ArrayIndexOutOfBoundsException("Attributes must be of equal length.");

        var newRows = new ArrayList<Comparable[]>();

        for (var these : this.tuples) {
            for (var those : table2.tuples) {
                boolean add = true;
                for (int i = 0; i < t_attrs.length; i++) {
                    var thisAttr = t_attrs[i];
                    var thatAttr = u_attrs[i];

                    var thisCol = this.col(thisAttr);
                    var thatCol = table2.col(thatAttr);

                    var thisVal = these[thisCol];
                    var thatVal = those[thatCol];

                    if (thisVal.compareTo(thatVal) != 0) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    var newRow = ArrayUtil.concat(these, those);
                    newRows.add(newRow);
                }
            }
        }

        return new Table(name + count++, renameDupeCols(table2),
                ArrayUtil.concat(domain, table2.domain), key, newRows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above, but implemented
     * using an Index Join algorithm.
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     */
    public Table i_join(String attributes1, String attributes2, Table table2) {
        return null;
    } // i_join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above, but implemented
     * using a Hash Join algorithm.
     *
     * @author David Luo
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     */
    public Table h_join(String attributes1, String attributes2, Table table2) {
        out.println("RA> " + name + ".h_join (" + attributes1 + ", " + attributes2 + ", " + table2.name + ")");

        var theseAttrs = attributes1.split(" ");
        var thoseAttrs = attributes2.split(" ");

        if (theseAttrs.length != thoseAttrs.length)
            throw new ArrayIndexOutOfBoundsException("Attributes must be of equal length.");

        var newRows = hJoinImpl(table2, theseAttrs, thoseAttrs);

        return new Table(name + count++, renameDupeCols(table2),
                ArrayUtil.concat(domain, table2.domain), key, newRows);
    } // h_join

    /**
     * Implementation of hash join for use in h_join and natural join
     *
     * @param table2     the other table to join
     * @param theseAttrs attributes on this table to check
     * @param thoseAttrs attributes on the other table to check
     * @return the rows of the new table.
     * @author David Luo
     */
    private List<Comparable[]> hJoinImpl(Table table2, String[] theseAttrs, String[] thoseAttrs) {
        var newRows = new ArrayList<Comparable[]>();

        // hashes = [{value: rows with that value}... for each attribute]
        var hashes = new ArrayList<HashMap<Comparable, List<Comparable[]>>>();
        for (String attr : theseAttrs) {
            var attrHash = new HashMap<Comparable, List<Comparable[]>>();

            int col = this.col(attr);
            for (Comparable[] row : this.tuples) {
                var valRows = attrHash.get(row[col]);
                if (valRows == null) {
                    valRows = new ArrayList<>();
                    valRows.add(row);
                    attrHash.put(row[col], valRows);
                } else {
                    valRows.add(row);
                }
            }
            hashes.add(attrHash);
        }

        for (var thatRow : table2.tuples) {
            HashSet<List<Comparable>> toAdd = new HashSet<>();
            for (int i = 0; i < thoseAttrs.length; i++) {
                String attr = thoseAttrs[i];
                var attrHash = hashes.get(i);
                var col = table2.col(attr);
                if (attrHash.containsKey(thatRow[col])) {
                    var theseRows = attrHash.get(thatRow[col]);
                    theseRows.forEach(it -> toAdd.add(Arrays.asList(it)));
                }
            }
            toAdd.forEach(it -> newRows.add(ArrayUtil.concat(it.toArray(new Comparable[0]), thatRow)));
        }
        return newRows;
    }

    /**
     * Helper method to rename duplicate columns for equi-join.
     *
     * @param table2 the table to join
     * @return the new renamed attributes
     * @author David Luo
     */
    private String[] renameDupeCols(Table table2) {
        var newAttrs = new ArrayList<>(Arrays.asList(this.attribute));
        var attrSet = new HashSet<>(Arrays.asList(this.attribute));
        String[] table2Attribute = table2.getAttribute();
        for (String attr : table2Attribute)
            if (attrSet.contains(attr))
                newAttrs.add(attr + "2");
            else
                newAttrs.add(attr);

        return newAttrs.toArray(new String[0]);
    }

    /************************************************************************************
     * Join this table and table2 by performing an "natural join".  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     *
     * Internally uses h_join.
     *
     * @author David Luo
     *
     * #usage movieStar.join (starsIn)
     *
     * @param table2  the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     */
    public Table join(Table table2) {
        out.println("RA> " + name + ".join (" + table2.name + ")");

        var theseAttrs = new HashSet<>(Arrays.asList(this.getAttribute()));
        var thoseAttrs = new HashSet<>(Arrays.asList(table2.getAttribute()));

        var commonAttrs = new HashSet<>(theseAttrs);
        commonAttrs.retainAll(thoseAttrs);

        var attributes = commonAttrs.toArray(new String[0]);

        var dupeIndicies = removeDupeCols(table2);
        dupeIndicies.sort((it1, it2) -> -it1.compareTo(it2));

        var newAttributes = new ArrayList<>(Arrays.asList(ArrayUtil.concat(this.getAttribute(), table2.getAttribute())));
        var newDomains = new ArrayList<>(Arrays.asList(ArrayUtil.concat(this.getDomain(), table2.getDomain())));
        var newRows = this.hJoinImpl(table2, attributes, attributes);
        for (int index : dupeIndicies) {
            newAttributes.remove(index);
            newDomains.remove(index);
        }

        for (int i = 0; i < newRows.size(); i++) {
            var rowAsList = new ArrayList<>(Arrays.asList(newRows.get(i)));
            for (int index : dupeIndicies)
                rowAsList.remove(index);
            newRows.set(i, rowAsList.toArray(new Comparable[0]));
        }

        // FIX - eliminate duplicate columns
        return new Table(name + count++, newAttributes.toArray(new String[0]), newDomains.toArray(new Class[0]), key, newRows);
    } // join

    /**
     * Helper method to remove duplicate columns
     *
     * @param table2 the table to join
     * @return an array of indices to delete in the new table
     * @author David Luo
     */
    private List<Integer> removeDupeCols(Table table2) {
        var theseAttrs = new HashSet<>(Arrays.asList(this.getAttribute()));

        var indicies = new ArrayList<Integer>();

        var index = this.getAttribute().length;
        for (var attr : table2.getAttribute()) {
            if (theseAttrs.contains(attr))
                indicies.add(index);
            index++;
        }

        return indicies;
    }

    /************************************************************************************
     * Return the column position for the given attribute name.
     *
     * @param attr  the given attribute name
     * @return a column position
     */
    public int col(String attr) {
        for (var i = 0; i < attribute.length; i++) {
            if (attr.equals(attribute[i])) return i;
        } // for

        return -1;  // not found
    } // col

    /************************************************************************************
     * Insert a tuple to the table.
     *
     * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup  the array of attribute values forming the tuple
     * @return whether insertion was successful
     */
    public boolean insert(Comparable[] tup) {
        out.println("DML> insert into " + name + " values ( " + Arrays.toString(tup) + " )");

        if (typeCheck(tup)) {
            tuples.add(tup);
            var keyVal = new Comparable[key.length];
            var cols = match(key);
            for (var j = 0; j < keyVal.length; j++) keyVal[j] = tup[cols[j]];
            if (mType != MapType.NO_MAP) index.put(new KeyType(keyVal), tup);
            return true;
        } else {
            return false;
        } // if
    } // insert

    /************************************************************************************
     * Get the name of the table.
     *
     * @return the table's name
     */
    public String getName() {
        return name;
    } // getName

    /************************************************************************************
     * Print this table.
     */
    public void print() {
        out.println("\n Table " + name);
        out.print("|-");
        out.print("---------------".repeat(attribute.length));
        out.println("-|");
        out.print("| ");
        for (var a : attribute) out.printf("%15s", a);
        out.println(" |");
        out.print("|-");
        out.print("---------------".repeat(attribute.length));
        out.println("-|");
        for (var tup : tuples) {
            out.print("| ");
            for (var attr : tup) out.printf("%15s", attr);
            out.println(" |");
        } // for
        out.print("|-");
        out.print("---------------".repeat(attribute.length));
        out.println("-|");
    } // print

    /************************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex() {
        out.println("\n Index for " + name);
        out.println("-------------------");
        if (mType != MapType.NO_MAP) {
            for (var e : index.entrySet()) {
                out.println(e.getKey() + " -> " + Arrays.toString(e.getValue()));
            } // for
        } // if
        out.println("-------------------");
    } // printIndex

    /************************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name  the name of the table to load
     */
    public static Table load(String name) {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DIR + name + EXT));
            tab = (Table) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            out.println("load: IO Exception");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            out.println("load: Class Not Found Exception");
            ex.printStackTrace();
        } // try
        return tab;
    } // load

    /************************************************************************************
     * Save this table in a file.
     */
    public void save() {
        try {
            var oos = new ObjectOutputStream(new FileOutputStream(DIR + name + EXT));
            oos.writeObject(this);
            oos.close();
        } catch (IOException ex) {
            out.println("save: IO Exception");
            ex.printStackTrace();
        } // try
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table2  the rhs table
     * @return whether the two tables are compatible
     */
    private boolean compatible(Table table2) {
        if (domain.length != table2.domain.length) {
            out.println("compatible ERROR: table have different arity");
            return false;
        } // if
        for (var j = 0; j < domain.length; j++) {
            if (domain[j] != table2.domain[j]) {
                out.println("compatible ERROR: tables disagree on domain " + j);
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /************************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column  the array of column names
     * @return an array of column index positions
     */
    private int[] match(String[] column) {
        int[] colPos = new int[column.length];

        for (var j = 0; j < column.length; j++) {
            var matched = false;
            for (var k = 0; k < attribute.length; k++) {
                if (column[j].equals(attribute[k])) {
                    matched = true;
                    colPos[j] = k;
                } // for
            } // for
            if (!matched) {
                out.println("match: domain not found for " + column[j]);
            } // if
        } // for

        return colPos;
    } // match

    /************************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t       the tuple to extract from
     * @param column  the array of column names
     * @return a smaller tuple extracted from tuple t
     */
    private Comparable[] extract(Comparable[] t, String[] column) {
        var tup = new Comparable[column.length];
        var colPos = match(column);
        for (var j = 0; j < column.length; j++) tup[j] = t[colPos[j]];
        return tup;
    } // extract

    /************************************************************************************
     * Check the size of the tuple (number of elements in list) as well as the type of
     * each value to ensure it is from the right domain.
     *
     * @author David Luo
     *
     * @param t  the tuple as a list of attribute values
     * @return whether the tuple has the right size and values that comply
     *          with the given domains
     */
    private boolean typeCheck(Comparable[] t) {
        if (t.length != this.domain.length)
            return false;
        for (int i = 0; i < this.domain.length; i++) {
            if (t[i].getClass() != this.domain[i]) {
                return false;
            }
        }
        return true;
    } // typeCheck

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className  the array of class name (e.g., {"Integer", "String"})
     * @return an array of Java classes
     */
    private static Class[] findClass(String[] className) {
        var classArray = new Class[className.length];

        for (var i = 0; i < className.length; i++) {
            try {
                classArray[i] = Class.forName("java.lang." + className[i]);
            } catch (ClassNotFoundException ex) {
                out.println("findClass: " + ex);
            } // try
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos the column positions to extract.
     * @param group  where to extract from
     * @return the extracted domains
     */
    private Class[] extractDom(int[] colPos, Class[] group) {
        var obj = new Class[colPos.length];

        for (var j = 0; j < colPos.length; j++) {
            obj[j] = group[colPos[j]];
        } // for

        return obj;
    } // extractDom

    public String[] getAttribute() {
        return attribute;
    }

    public List<Comparable[]> getTuples() {
        return tuples;
    }

    public String[] getKey() {
        return key;
    }

    public Class[] getDomain() {
        return domain;
    }

    /**
     * Checks for equality, ignoring the name of the table
     *
     * @param obj the object to compare
     * @return is equal?
     * @author David Luo
     */
    public boolean equalsIgnoreName(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Table))
            return false;
        var other = (Table) obj;
        if (!Arrays.equals(this.getAttribute(), other.getAttribute()))
            return false;
        if (!Arrays.equals(this.getKey(), other.getKey()))
            return false;

        Set theseTuples = new HashSet<>();
        this.getTuples().forEach(it -> theseTuples.add(Arrays.asList(it)));
        Set otherTuples = new HashSet<>();
        other.getTuples().forEach(it -> otherTuples.add(Arrays.asList(it)));

        if (!theseTuples.equals(otherTuples))
            return false;
        return true;
    }

    /**
     * Checks for equality
     *
     * @param obj the object to compare
     * @return is equal?
     * @author David Luo
     */
    @Override
    public boolean equals(Object obj) {
        if (!this.equalsIgnoreName(obj))
            return false;
        var other = (Table) obj;
        if (!this.getName().equals(other.getName()))
            return false;
        return true;
    }
} // Table class

