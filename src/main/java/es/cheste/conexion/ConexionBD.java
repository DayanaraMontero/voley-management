package es.cheste.conexion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Esta clase gestiona la conexión a una base de datos a través de JDBC utilizando un archivo de
 * propiedades para almacenar las configuraciones de la conexión.
 *
 * <p>Implementa un método para conectar a la base de datos y otro para desconectarse manteniendo la
 * conexión abierta mientras se necesite.</p>
 */
public class ConexionBD {
    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final String FILENAME = "src/main/resources/application.properties";
    private Connection connection = null;

    /**
     * Establece una conexión a la base de datos utilizando los parámetros cargados desde el archivo de propiedades.
     *
     * <p>Este método carga el archivo específicado para obtener la URL, el usuario y la contraseña necesarios
     * para conectarse a la base de datos.</p>
     */
    private void conectar() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream((FILENAME)));

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            LOGGER.error("Error para cargar el archivo de propiedades.", e);
        } catch (SQLException e) {
            LOGGER.error("Error conectando a la base de datos.", e);
        }
    }

    /**
     * Este método se encarga de desconectar la conexión activa con la base de datos.
     */
    public void desconectar() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Error para desconectar de la base de datos", e);
        }
    }

    /**
     * Devuelve la conexión activa a la base de datos.
     *
     * <p>Si la conexión aún no está establecida, se llama a este método para
     * crear una nueva conexión. Esto asegura que siempre se devuelva una conexión
     * válida cuando sea posible</p>
     *
     * @return la conexión activa a la base de datos.
     */
    public Connection getConnection() {
        if (connection == null) {
            conectar();
        }
        return connection;
    }
}
