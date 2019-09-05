# CSCI 4370 Project 1

## Group Info
Team Name: Sequel To SQL

Members:
Ravi Parashar     811256698
David Luo         811357331
Miruna Cristian   811048548
Devan Vitha (M)   811055508

## Building

Building is done using `sbt`.

|Task   |   In terminal  |In `sbt` console|
|-------|----------------|----------------|
|compile|`sbt compile`   |`compile`       |
|run    |`sbt run`       |`run`           |
|test   |`sbt test`      |`test`          |

When running select `[2] MovieDB`.

## Work Division
David Luo:
* `join` (nested loop equijoin)
* `h_join` (hash table equijoin)
* `join` (natural join)
* `typeCheck`
* `TestJoin.java`
* `build.sbt`
* along with a few helper methods.

Ravi Parashar:
* Union() 
* Minus() 
* TestUnionMinus.java

Miruna Cristian:
* Project()
* Select()
* TestProjectSelect.java

Devan Vitha
* Integration Testing
* General debuging testing
* javadoc and UML diagrams 
