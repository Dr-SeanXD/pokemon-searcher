/**
 * @Author       Sean Lin
 * @File_Name    PokemonServlet.java
 * @Date_Created Apr. 10th, 2025
 * @Project      HW#6 - Pokemon Servlet Searcher
 * @Description  This program is a simple web application run by using Java Servlet. It asks inputs
 *               from the users, specifically Pokémon IDs, and response with a page of the
 *               Pokémon's information, including name and type(s).
 */

//This file is inside the edu.bhcc package.
package edu.bhcc;

//Import for the IOException and InputStream.
import java.io.*;

//Importing the classes needed for SQLite and the SQLite JDBC Driver.
import java.sql.*;

//Importing the utilities needed for the program.
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

//Import the classes needed for Java Servlet.
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * PokemonServlet class that extends HttpServlet, including the doGet method that will handle
 * requests from users.
 */
public class PokemonServlet extends HttpServlet {

    //Initializes the database name.
    private final String DBNAME = "jdbc:sqlite:pokemon.db";

    //Initializes the table's name in the database.
    private final String TABLE = "pokemon";

    //Declare the Connection of the database.
    private Connection connection;

    //Declare the Statement for the database.
    private Statement statement;

    /**
     * This method initializes the Connection and Statement for the JDBC database.
     *
     * @throws SQLException If a database access error occurs or the url is invalid.
     */
    private void initDataBase() throws SQLException {
        connection = DriverManager.getConnection(DBNAME);
        statement = connection.createStatement();
    }

    /**
     * This method formats Strings to ones that the database can read.
     *
     * @param id The String that will be formatted.
     * @return The String formatted with # and 0(s) if needed.
     */
    private String format_pokemon_id(String id) {
        String result = "#";
        if (id.length() == 1) {
            result += "000";
        }
        else if (id.length() == 2) {
            result += "00";
        }
        else if (id.length() == 3) {
            result += "0";
        }
        result += id;
        return result;
    }

    /**
     * Query the Pokémon's data using the ID provided in the parameter. Since there might be
     * multiple regional forms (different Pokémon) associated with the same ID, the method will
     * return an ArrayList that includes the Pokémon's data. Each HashMap represents a Pokémon.
     *
     * @param pokemon_id The ID that will be used to search in the database.
     * @return The ArrayList containing one or multiple Pokémon information.
     * @throws SQLException If a database access error occurs or the url is invalid.
     */
    private ArrayList<HashMap<String, String>> queryPokemon(String pokemon_id)
            throws SQLException {
        //Initializes the query statement.
        String query = String.format("SELECT * FROM %s WHERE id='%s'", TABLE, pokemon_id);

        //Using a ResultSet and the Statement initialized above, query the data from the database.
        ResultSet resultSet = statement.executeQuery(query);

        //Initializes an ArrayList that will store Pokémon data and be returned.
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        //Using a while loop, traverse through all searched (found) rows.
        while (resultSet.next()) {
            //Store the corresponding information in a HashMap with the correct keys.
            HashMap<String, String> map = new HashMap<>();
            map.put("ID", resultSet.getString("id"));
            map.put("Name", resultSet.getString("name"));
            map.put("Types", resultSet.getString("types"));
            map.put("Form", resultSet.getString("form"));
            map.put("Image", resultSet.getString("image"));

            // If there is a form value, append it to the Name for easier reference.
            if (map.get("Form") != null && !map.get("Form").trim().isEmpty()) {
                map.put("Name", map.get("Name") + " (" + map.get("Form") + ")");
            }

            //Add the HashMap to the ArrayList.
            result.add(map);
        }

        //Close the ResultSet after the iteration.
        resultSet.close();

        //Return the ArrayList.
        return result;
    }

    /**
     * Using the pokemon_info provided, generate an array of image addresses for the Pokémon.
     *
     * @param pokemon_info The HashMap(s) that contain Pokémon information
     * @return The 2D ArrayList that contains image addresses for the types of the Pokémon.
     */
    private ArrayList<ArrayList<String>> processTypeImages
            (ArrayList<HashMap<String, String>> pokemon_info) {
        //Declare a 2D ArrayList that will store what will be returned.
        ArrayList<ArrayList<String>> typeImages = new ArrayList<>();

        //Declare an ArrayList that will store the types of the Pokémon.
        ArrayList<String[]> types = new ArrayList<>();

        //Using a for-loop, traverse through the pokemon_info.
        for (int i = 0; i<pokemon_info.size(); i++) {
            //Split the types into two Strings if there are 2 (maximum is 2).
            HashMap<String, String> map = pokemon_info.get(i);
            types.add(map.get("Types").split(", "));
        }

        /*
        Using a for-loop, traverse through the Pokémon's type and assign the type image addresses.
         */
        for (int i = 0; i<types.size(); i++) {
            String[] current = types.get(i);
            ArrayList<String> arrayList = new ArrayList<>();
            for (int j = 0; j<current.length; j++) {
                arrayList.add(String.format("/images/types/%s.png", current[j]));
            }
            typeImages.add(arrayList);
        }

        //Return the 2D ArrayList.
        return typeImages;
    }

    /**
     * Print the HTML files using the PrintWriter.
     *
     * @param writer The PrintWriter that will be used.
     * @param path The path of the HTML files.
     */
    private void printHTML(PrintWriter writer, String path) {
        InputStream inputStream = getServletContext().getResourceAsStream(path);

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Printing the Pokémon information via the PrintWriter.
     *
     * @param writer The PrintWriter that will be used.
     * @param pokemon_info The Pokémon information that will be printed.
     * @param typeImages The ArrayList that contains the type images.
     */
    private void printResult(PrintWriter writer, ArrayList<HashMap<String, String>> pokemon_info,
                             ArrayList<ArrayList<String>> typeImages) {
        writer.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>Pokemon Info</title>\n" +
                "    <style>\n" +
                "        body { font-family: \"Helvetica Neue\", sans-serif; font-size: 16px; " +
                "color: #333; }\n" +
                "        h1 { font-family: \"Segoe UI\", \"Helvetica Neue\", Arial, sans-serif; " +
                "font-size: 28px; color: #000; }\n" +
                "        h2 { font-family: Arial, sans-serif; font-size: 20px; color: #555; }\n" +
                "        button { font-family: Arial, sans-serif; font-size: 16px; }\n" +
                "    </style>\n" +
                "</head>");
        writer.println("<body>\n" +
                "    <button type=\"button\" " +
                "onclick=\"window.location.href='/pokemon'\"\n" +
                "            style=\"padding: 10px 20px; font-size: 16px; " +
                "font-family: Arial, sans-serif;\n" +
                "                   background-color: #4CAF50; color: white; border: none; " +
                "border-radius: 5px;\n" +
                "                   cursor: pointer;\">\n" +
                "        Back to Search\n" +
                "    </button>");
        for (int i = 0; i<pokemon_info.size(); i++) {
            HashMap<String, String> pokemon = pokemon_info.get(i);
            writer.println(String.format("<h1>%s</h1>", pokemon.get("Name")));
            writer.println(String.format("<img src=\"%s\" alt=\"%s image\" " +
                    "style=\"max-width:300px;\"/>", pokemon.get("Image"), pokemon.get("Name")));
            writer.println("<div style=\"border: 1px solid #ccc; padding: 10px; " +
                    "margin-bottom: 10px;\">");
            writer.println(String.format("<h2>ID: %s</h2>", pokemon.get("ID")));
            writer.println(String.format("<h2>Type(s): %s</h2>", pokemon.get("Types")));
            ArrayList<String> types = typeImages.get(i);
            for (int j = 0; j<types.size(); j++) {
                writer.println(String.format("<img src=\"%s\" alt=\"Type Image\" " +
                        "style=\"max-width:50px;\" />", types.get(j)));
            }
            writer.println("</div>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    /**
     * Check if the parameter is a number.
     *
     * @param pokemon_id The String that will be checked.
     * @return Whether the String parameter given is a number.
     */
    private boolean isNumber(String pokemon_id) {
        for (int i = 0; i<pokemon_id.length(); i++) {
            if (!Character.isDigit(pokemon_id.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Processes an HTTP Request, which is an ID of a Pokémon, and returns the corresponding data.
     *
     * @throws IOException If the sendRedirect has any errors.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        /*
        By calling the initDataBase() method, initializes the Connection and Statement to JDBC
        database.
         */
        try {
            initDataBase();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        //Set the MIME Type to text/html.
        response.setContentType("text/html");

        //Set the response code to 200 (OK).
        response.setStatus(HttpServletResponse.SC_OK);

        //Get the parameter "pokemon_id."
        String pokemon_id = request.getParameter("pokemon_id");

        PrintWriter writer = response.getWriter();

        //If ID is not present, redirects to the search page and returns.
        if (pokemon_id == null || pokemon_id.trim().isEmpty()) {
            printHTML(writer, "/pages/search.html");
            return;
        }

        //Initializes a valid boolean variable to true to see if the pokemon_id is valid.
        boolean valid = true;

        //If the ID is empty or isn't a number, sets the valid variable to false.
        if (pokemon_id.isEmpty() || !isNumber(pokemon_id)) {
            valid = false;
        }
        //If the ID is not empty:
        else {
            //Convert the String into an integer.
            int intID = Integer.parseInt(pokemon_id);

            /*
            If the ID is out of bounds (there are only 1025 Pokémon so far as of today), set the
            valid variable to false.
             */
            if (intID < 0 || intID > 1025) {
                valid = false;
            }
            //If the ID is valid, format the ID by calling the format_pokemon_id() method.
            else {
                pokemon_id = format_pokemon_id(pokemon_id);
            }
        }

        //If the pokemon_id provided is invalid, redirect to the error page.
        if (!valid) {
            printHTML(writer, "/pages/error.html");
            return;
        }

        //Declare an ArrayList that will store the Pokémon information.
        ArrayList<HashMap<String, String>> pokemon_info = null;

        //Try to query the data of the Pokémon by calling the queryPokemon() method.
        try {
            pokemon_info = queryPokemon(pokemon_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        By calling the processTypeImages() method, get the image addresses of the types of the
        Pokémon.
         */
        ArrayList<ArrayList<String>> typeImages = processTypeImages(pokemon_info);

        //By calling the printResult() method, write the jsp file using the PrintWriter.
        printResult(writer, pokemon_info, typeImages);
    }
}