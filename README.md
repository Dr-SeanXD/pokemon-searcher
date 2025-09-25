# Pokémon Searcher - Web Application

- **Author**: Sean Lin
- **Instructor**: Ethan Cerami
- **Course**: Advanced Java [CSC-285-01]
- **Project**: Pokémon Servlet Searcher (Homework #6)

---

This program is a simple web application run by using Java Servlet. It asks inputs from the users, 
specifically Pokémon IDs, and response with a page of the Pokémon's information, including name and 
type(s).

## Instructions

1. To run the server, run `mvn jetty:run`.
2. Go to [http://localhost:8080/pokemon](http://localhost:8080/pokemon).
3. Enter a Pokémon ID (just an integer), there are only 1025 Pokémon found so far, so the valid ID 
range will be **1 ~ 1025** (i.e., 128).
4. If the Pokémon ID entered is invalid, users will be redirected to an error page.
5. If the Pokémon ID is valid, the users will be able to see the information of the Pokémon
associated with the entered ID.
6. Users can always go back to the search page by pressing the `Back to Search` button at the top
of the Pokémon information (result) page.

### Note

There might be multiple regional variants of a Pokémon that are associated with the same Pokémon
ID. 

For instance, #0128 (Tauros) has its original form and three other Paldean Forms. In this case,
the result page will show both the Pokémon's original form and also their regional form(s) if any.

Otherwise, the result page will usually show one Pokémon's information, which is the one that its
ID is associated with according to the users' inputs.

## Requirements

### Maven Dependencies

This program was created using Maven, which uses JDBC and Java Servlet, so please put the following
dependencies in the `pom.xml` file.

```
<dependencies>
    <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>6.1.0</version>
      <scope>provided</scope>
    </dependency>

    <dependency>
      <groupId>org.xerial</groupId>
      <artifactId>sqlite-jdbc</artifactId>
      <version>3.49.1.0</version>
    </dependency>
  </dependencies>
```

### Maven Build Plugin

This program uses Jetty to run, so please include the following inside the build section of the
`pom.xml` file.

```
<plugins>
    <plugin>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
        <version>11.0.20</version>
    </plugin>
</plugins>
```

### Java JDK Version

This program is run on **Java JDK 22**, which is not the latest JDK Version, according to 
[**Oracle**](https://www.oracle.com/java/technologies/downloads/). Therefore, modification(s) on
the `pom.xml` file and the project structure might be necessary to run the program/server.

## References

This program can only be done because of the `pokemon.db` database that was obtained via
[**Bulbapedia**](https://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number)
using Python.

### SQLite and JDBC

The `pokemon.db` file is modified using SQLite and accessed in Java via JDBC. The SQL statement for
creating the table `pokemon` in the database is the following:

```
CREATE TABLE IF NOT EXISTS pokemon (
    id TEXT,
    name TEXT,
    types TEXT,
    form TEXT,
    image TEXT
);
```

### Pokémon Data & Images

As mentioned above, the Pokémon data (alongside their images) was obtained via
[**Bulbapedia**](https://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number)
, and the credits to the Pokémon type images again from 
[**Bulbapedia**](https://archives.bulbagarden.net/w/index.php?title=Category:Type_icons&fileuntil=FireIC+XD.png#mw-category-media).