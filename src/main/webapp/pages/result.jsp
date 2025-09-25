<%-- Result page that will show the Pokémon data --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Pokemon Info</title>
    <style>
        body { font-family: "Helvetica Neue", sans-serif; font-size: 16px; color: #333; }
        h1 { font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif; font-size: 28px; color: #000; }
        h2 { font-family: Arial, sans-serif; font-size: 20px; color: #555; }
        button { font-family: Arial, sans-serif; font-size: 16px; }
    </style>
</head>
<body>
    <%-- A button that allows users to go back to the search page --%>
    <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/pokemon'"
            style="padding: 10px 20px; font-size: 16px; font-family: Arial, sans-serif;
                   background-color: #4CAF50; color: white; border: none; border-radius: 5px;
                   cursor: pointer;">
        Back to Search
    </button>

    <%-- Traverse through the array passed in --%>
    <c:forEach var="pokemon" items="${sessionScope.pokemonInfo}" varStatus="status">
        <%-- Display the Pokémon information --%>
        <h1>${pokemon.Name}</h1>
        <img src="${pokemon.Image}" alt="${pokemon.Name} image" style="max-width:300px;"/>
        <div style="border: 1px solid #ccc; padding: 10px; margin-bottom: 10px;">
            <h2>ID: ${pokemon.ID}</h2>
            <h2>Type(s): ${pokemon.Types}</h2>

            <%-- This is another loop that will traverse through the type array and display the
                image --%>
            <c:forEach var="image" items="${sessionScope.typeImages[status.index]}">
                <img src="${pageContext.request.contextPath}${image}" alt="Type Image" style="max-width:50px;" />
            </c:forEach>
        </div>
    </c:forEach>
    <%
        session.removeAttribute("pokemonInfo");
    %>
</body>
</html>