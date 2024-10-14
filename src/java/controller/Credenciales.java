package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import configuration.ConnectionBD;
import java.io.IOException;

// Especifica la ruta para acceder al servlet
@WebServlet("/credenciales")
public class Credenciales extends HttpServlet {
    
    // Declaración de los atributos solicitados
    Connection conn;
    PreparedStatement ps;
    Statement statement;
    ResultSet rs;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        // Obtener los parámetros del formulario
        String matricula = request.getParameter("matricula");
        String password = request.getParameter("password");

        try {
            // Hashear la contraseña usando bcrypt
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Conectar a la base de datos
            ConnectionBD conexion = new ConnectionBD();
            String sql = "INSERT INTO autenticacion (matricula, password) VALUES (?, ?)";

            conn = conexion.getConnectionBD();
            ps = conn.prepareStatement(sql);
            ps.setString(1, matricula);
            ps.setString(2, hashPassword);

            // Ejecutar la consulta
            int filasInsertadas = ps.executeUpdate();

            // Si se insertó correctamente, redirigir al usuario a una página de éxito
            if (filasInsertadas > 0) {
                request.setAttribute("mensaje", "Usuario registrado con éxito!");
                request.getRequestDispatcher("/jsp/mensaje.jsp").forward(request, response);
            } else {
                // Si falló, redirigir a una página de error
                request.setAttribute("mensaje", "Error al registrar usuario.");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            }

            // Cerrar conexiones
            ps.close();
            conn.close();

        } catch (Exception e) {
            // Manejo de errores
            request.setAttribute("error", e);
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }
}
