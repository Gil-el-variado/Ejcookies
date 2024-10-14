package controller;

import configuration.ConnectionBD;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Gilberto
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login_servlet"})
public class LoginServlet extends HttpServlet {

    // Declaración de las variables
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtener la matrícula y password del formulario
        String matricula = request.getParameter("matricula");
        String password = request.getParameter("password");

        try {
            // Conectar a la base de datos
            ConnectionBD conexion = new ConnectionBD();
            conn = conexion.getConnectionBD();

            // Consulta a la base de datos
            String sql = "SELECT password FROM autenticacion WHERE matricula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, matricula);

            rs = ps.executeQuery();

            if (rs.next()) {
                // Obtener el password almacenado y compararlo con el ingresado
                String hashPassword = rs.getString("password");

                if (BCrypt.checkpw(password, hashPassword)) {
                    // Si la contraseña es correcta, crear la cookie
                    Cookie cookie = new Cookie("matricula", matricula);
                    cookie.setPath("/"); // Establecer el Path para toda la aplicación

                    if (request.getParameter("recordar") != null && request.getParameter("recordar").equals("on")) {
                        // Cookie con duración de 24 horas
                        cookie.setMaxAge(60 * 60 * 24); // 24 horas
                    } else {
                        // Cookie con duración de 5 minutos
                        cookie.setMaxAge(60 * 5); // 5 minutos
                    }

                    // Agregar la cookie a la respuesta
                    response.addCookie(cookie);

                    // Redirigir al usuario a la página de inicio
                    request.getRequestDispatcher("/jsp/usuario.jsp").forward(request, response);

                } else {
                    // Contraseña incorrecta, redirigir a error.jsp
                    request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                }
            } else {
                // Matrícula no encontrada, redirigir a error.jsp
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            }

            // Cerrar conexiones
            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            // Manejo de errores
            System.out.println("Error: " + e);
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    // Método doGet para eliminar cookies y cerrar sesión
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Invalidar la sesión del usuario si existe
        HttpSession session = request.getSession(false); // No crear sesión nueva
        if (session != null) {
            session.invalidate(); // Invalidar la sesión existente
        }

        // Eliminar la cookie llamada 'matricula'
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("matricula".equals(cookie.getName())) {
                    cookie.setMaxAge(0); // Expirar la cookie
                    cookie.setPath("/"); // Asegurar que el Path coincida
                    response.addCookie(cookie); // El navegador eliminará la cookie
                    break;
                }
            }
        }

        // Redirigir al login
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
    }
}
