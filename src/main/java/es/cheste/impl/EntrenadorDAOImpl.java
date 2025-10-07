package es.cheste.impl;

import es.cheste.clases.Entrenador;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.EntrenadorDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar entrenadores en la base de datos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link EntrenadorDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Entrenador} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Entrenador
 * @see EntrenadorDAO
 */
public class EntrenadorDAOImpl implements EntrenadorDAO {
    private static final String INSERTAR = "INSERT INTO ENTRENADOR (NOMBRE, APELLIDOS, NACIONALIDAD, EXPERIENCIA) VALUES (?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM ENTRENADOR WHERE ID_ENTRENADOR = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM ENTRENADOR";
    private static final String ACTUALIZAR = "UPDATE ENTRENADOR SET NOMBRE = ?, APELLIDOS = ?, NACIONALIDAD = ?, EXPERIENCIA = ? WHERE ID_ENTRENADOR = ?";
    private static final String ELIMINAR = "DELETE FROM ENTRENADOR WHERE ID_ENTRENADOR = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta un nuevo entrenador en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de un entrenador a la base de datos, si la consulta funciona
     * correctamente se asigna el ID correspondiente al entrenador.</p>
     *
     * @param entrenador es el objeto {@link Entrenador} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar el entrenador.
     */
    @Override
    public void insertar(Entrenador entrenador) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entrenador.getNombre());
            ps.setString(2, entrenador.getApellidos());
            ps.setString(3, entrenador.getNacionalidad());
            ps.setInt(4, entrenador.getExperiencia());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se ha podido insertar el entrenador, no se afectaron filas.", null);
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entrenador.setIdEntrenador(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar los datos del entrenador.", e);
        }
    }

    /**
     * Obtiene un entrenador de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de un entrenador con el ID correspondiente.
     * Si se encuentra el registro, se crea un objeto {@link Entrenador} con los datos recuperados.</p>
     *
     * @param id el ID del entrenador que se desea obtener.
     * @return un objeto {@link Entrenador} si se encuentra el entrenador, en caso contrario, devuelve un valor null.
     * @throws DAOException si ocurre un error SQL al recuperar el entrenador.
     */
    @Override
    public Entrenador obtenerPorId(int id) throws DAOException {
        Entrenador entrenador = null;

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entrenador = mapearEntrenador(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener el entrenador por ID.", e);
        }
        return entrenador;
    }

    /**
     * Obtiene todos los entrenadores de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla ENTRENADOR.
     * Los registros recuperados se agregan a una lista de objetos {@link Entrenador}.</p>
     *
     * @return una lista de objetos {@link Entrenador} que representa todos los entrenadores en la base de datos.
     * @throws DAOException sí ocurre un error SQL al recuperar los entrenadores.
     */
    @Override
    public List<Entrenador> obtenerTodos() throws DAOException {
        List<Entrenador> entrenadorList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Entrenador entrenador = mapearEntrenador(rs);
                entrenadorList.add(entrenador);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los entrenadores.", e);
        }
        return entrenadorList;
    }

    /**
     * Actualiza la información de un entrenador en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de un entrenador a partir de su ID.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param entrenador el objeto {@link Entrenador} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar el entrenador.
     */
    @Override
    public void actualizar(Entrenador entrenador) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setString(1, entrenador.getNombre());
            ps.setString(2, entrenador.getApellidos());
            ps.setString(3, entrenador.getNacionalidad());
            ps.setInt(4, entrenador.getExperiencia());
            ps.setInt(5, entrenador.getIdEntrenador());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de entrenador fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla ENTRENADOR correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla ENTRENADOR.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla ENTRENADOR: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar el entrenador.", e);
        }
    }

    /**
     * Elimina un entrenador de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de un entrenador con el ID correspondiente.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param id el ID del entrenador que se desea eliminar.
     * @throws DAOException sí ocurre un error SQL al eliminar el entrenador.
     */
    @Override
    public void eliminar(int id) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación del entrenador fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla ENTRENADOR correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla ENTRENADOR.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla ENTRENADOR: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar el entrenador.", e);
        }
    }

    /**
     * Crea un objeto {@link Entrenador} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Entrenador} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos del entrenador.
     * @return un objeto {@link Entrenador} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Entrenador mapearEntrenador(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_ENTRENADOR");
        String nombre = rs.getString("NOMBRE");
        String apellidos = rs.getString("APELLIDOS");
        String nacionalidad = rs.getString("NACIONALIDAD");
        int experiencia = rs.getInt("EXPERIENCIA");

        return new Entrenador(id, nombre, apellidos, nacionalidad, experiencia);
    }
}
