package es.cheste.impl;

import es.cheste.clases.Usuario;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.UsuarioDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar usuarios en la base de datos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link UsuarioDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Usuario} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Usuario
 * @see UsuarioDAO
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    private static final String INSERTAR = "INSERT INTO USUARIO (NOMBRE, APELLIDOS, EMAIL, CONTRASENYA, ID_JUGADORA) VALUES (?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM USUARIO WHERE ID_USUARIO = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM USUARIO";
    private static final String ACTUALIZAR = "UPDATE USUARIO SET NOMBRE = ?, APELLIDOS = ?, EMAIL = ?, CONTRASENYA = ?, ID_JUGADORA = ? WHERE ID_USUARIO = ?";
    private static final String ELIMINAR = "DELETE FROM USUARIO WHERE ID_USUARIO = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de un usuario a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente al usuario.</p>
     *
     * @param usuario es el objeto {@link Usuario} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar el usuario.
     */
    @Override
    public void insertar(Usuario usuario) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getContrasenya());
            ps.setInt(5, usuario.getIdJugadora());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se han podido insertar los datos del usuario, no se afectaron filas.", null);
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar el usuario.", e);
        }
    }

    /**
     * Obtiene un usuario de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de un usuario con el ID correspondiente.
     * Si se encuentra el registro, se crea un objeto {@link Usuario} con los datos recuperados.</p>
     *
     * @param id el ID del usuario que se desea obtener.
     * @return un objeto {@link Usuario} si se encuentra el usuario, en caso contrario, devuelve un valor null.
     * @throws DAOException si ocurre un error SQL al recuperar el usuario.
     */
    @Override
    public Usuario obtenerPorId(int id) throws DAOException {
        Usuario usuario = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener el usuario por ID.", e);
        }
        return usuario;
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla USUARIO.
     * Los registros recuperados se agregan a una lista de objetos {@link Usuario}.</p>
     *
     * @return una lista de objetos {@link Usuario} que representa todos los usuarios en la base de datos.
     * @throws DAOException si ocurre un error SQL al recuperar los usuarios.
     */
    @Override
    public List<Usuario> obtenerTodos() throws DAOException {
        List<Usuario> usuarioList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = mapearUsuario(rs);
                usuarioList.add(usuario);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los usuarios.", e);
        }
        return usuarioList;
    }

    /**
     * Actualiza la información de un usuario en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de un usuario a partir de su ID.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param usuario el objeto {@link Usuario} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar el usuario.
     */
    @Override
    public void actualizar(Usuario usuario) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getContrasenya());
            ps.setInt(5, usuario.getIdJugadora());
            ps.setInt(6, usuario.getIdUsuario());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de usuario fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla USUARIO correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla USUARIO.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla USUARIO: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar el usuario.", e);
        }
    }

    /**
     * Elimina un usuario de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de un usuario con el ID correspondiente.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param id el ID del usuario que se desea eliminar.
     * @throws DAOException si ocurre un error SQL al eliminar el usuario.
     */
    @Override
    public void eliminar(int id) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {

            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación de usuario fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla USUARIO correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla USUARIO.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla USUARIO: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar el usuario.", e);
        }
    }

    /**
     * Crea un objeto {@link Usuario} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Usuario} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos del usuario.
     * @return un objeto {@link Usuario} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int idUsuario = rs.getInt("ID_USUARIO");
        String nombre = rs.getString("NOMBRE");
        String apellidos = rs.getString("APELLIDOS");
        String email = rs.getString("EMAIL");
        String contrasenya = rs.getString("CONTRASENYA");
        int idJugadora = rs.getInt("ID_JUGADORA");

        return new Usuario(idUsuario, nombre, apellidos, email, contrasenya, idJugadora);
    }
}
