<%-- 
    Document   : usuario
    Created on : 6/10/2024, 10:52:36 PM
    Author     : Gilberto
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Inicio Usuario</title>

        <%
            // Código para obtener la cookie de matrícula
            Cookie[] cookies = request.getCookies();
            String valor = "";
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("matricula")) {
                        valor = c.getValue();
                        break;
                    }
                }
            }
        %>
    </head>

    <body>
        <h1>Bienvenido <%= valor %>, ha iniciado sesión de manera correcta. </h1>
        <!-- Enlace para cerrar sesión apuntando al servlet -->
        <a href="${pageContext.request.contextPath}/login_servlet">Cerrar Sesión</a>
    </body>

</html>
