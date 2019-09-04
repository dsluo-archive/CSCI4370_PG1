// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

// Compile with preview features enabled
// javacOptions ++= Seq("--enable-preview", "-source", "12")

// To run with preview features, set JAVA_OPTS='--enable-preview'
// in your shell.

// Set up JUnit
// http://mvnrepository.com/artifact/junit/junit
libraryDependencies += "junit" % "junit" % "4.12"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

