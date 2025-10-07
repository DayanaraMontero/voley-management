package es.cheste.impl;

import es.cheste.clases.Estadistica;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.EstadisticaDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar estadísticas en la base de datos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link EstadisticaDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Estadistica} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Estadistica
 * @see EstadisticaDAO
 */
public class EstadisticaDAOImpl implements EstadisticaDAO {
    private static final String INSERTAR = "INSERT INTO ESTADISTICA (ID_JUGADORA, ID_PARTIDO, ATAQUES, SAQUES, BLOQUEOS, DEFENSAS, ERRORES) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM ESTADISTICA WHERE ID_ESTADISTICA = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM ESTADISTICA";
    private static final String ACTUALIZAR = "UPDATE ESTADISTICA SET ID_JUGADORA = ?, ID_PARTIDO = ?, ATAQUES= ?, SAQUES = ?, BLOQUEOS = ?, DEFENSAS = ?, ERRORES = ? WHERE ID_ESTADISTICA = ?";
    private static final String ELIMINAR = "DELETE FROM ESTADISTICA WHERE ID_ESTADISTICA = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta una nueva estadistica en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de una estadística a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente a la estadística.</p>
     *
     * @param estadistica es el objeto {@link Estadistica} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar la estadística.
     */
    @Override
    public void insertar(Estadistica estadistica) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, estadistica.getIdJugadora());
            ps.setInt(2, estadistica.getIdPartido());
            ps.setInt(3, estadistica.getAtaques());
            ps.setInt(4, estadistica.getSaques());
            ps.setInt(5, estadistica.getBloqueos());
            ps.setInt(6, estadistica.getDefensas());
            ps.setInt(7, estadistica.getErrores());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se han podido insertar los datos de la estadistica, no se afectaron filas.", null);
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    estadistica.setIdEstadistica(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar los datos de la estadistica.", e);
        }
    }

    /**
     * Obtiene una estadística de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de una estadística con el ID correspondiente.
     * Si se encuentra el registro, se crea un objeto {@link Estadistica} con los datos recuperados.</p>
     *
     * @param id el ID de la estadística que se desea obtener.
     * @return un objeto {@link Estadistica} si se encuentra la estadística, en caso contrario, devuelve un valor null.
     * @throws DAOException si ocurre un error SQL al recuperar la estadística.
     */
    @Override
    public Estadistica obtenerPorId(int id) throws DAOException {
        Estadistica estadistica = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener los datos de las estadisticas por ID.", e);
        }
        return estadistica;
    }

    /**
     * Obtiene todas las estadísticas de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla ESTADISTICA.
     * Los registros recuperados se agregan a una lista de objetos {@link Estadistica}.</p>
     *
     * @return una lista de objetos {@link Estadistica} que representa todas las estadísticas en la base de datos.
     * @throws DAOException sí ocurre un error SQL al recuperar las estadísticas.
     */
    @Override
    public List<Estadistica> obtenerTodos() throws DAOException {
        List<Estadistica> estadisticaList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estadistica estadistica = mapearEstadistica(rs);
                estadisticaList.add(estadistica);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los datos de las estadisticas.", e);
        }
        return estadisticaList;
    }

    /**
     * Actualiza la información de una estadística en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de una estadística a partir de su ID.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param estadistica el objeto {@link Estadistica} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar la estadística.
     */
    @Override
    public void actualizar(Estadistica estadistica) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, estadistica.getIdJugadora());
            ps.setInt(2, estadistica.getIdPartido());
            ps.setInt(3, estadistica.getAtaques());
            ps.setInt(4, estadistica.getSaques());
            ps.setInt(5, estadistica.getBloqueos());
            ps.setInt(6, estadistica.getDefensas());
            ps.setInt(7, estadistica.getErrores());
            ps.setInt(8, estadistica.getIdEstadistica());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de los datos de las estadisticas fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla ESTADISTICA correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla ESTADISTICA.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla ESTADISTICA: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar los datos de las estadisticas.", e);
        }
    }

    /**
     * Elimina una estadística de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de una estadística con el ID correspondiente.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param id el ID de la estadística que se desea eliminar.
     * @throws DAOException si ocurre un error SQL al eliminar la estadística.
     */
    @Override
    public void eliminar(int id) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación de los datos de las estadisticas fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar en la tabla ESTADISTICA correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla ESTADISTICA.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla ESTADISTICA: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar los datos de las estadisticas.", e);
        }
    }

    /**
     * Crea un objeto {@link Estadistica} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Estadistica} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos de la estadística.
     * @return un objeto {@link Estadistica} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Estadistica mapearEstadistica(ResultSet rs) throws SQLException {
        int idEstadistica = rs.getInt("ID_ESTADISTICA");
        int idJugadora = rs.getInt("ID_JUGADORA");
        int idPartido = rs.getInt("ID_PARTIDO");
        int ataques = rs.getInt("ATAQUES");
        int saques = rs.getInt("SAQUES");
        int bloqueos = rs.getInt("BLOQUEOS");
        int defensas = rs.getInt("DEFENSAS");
        int errores = rs.getInt("ERRORES");

        return new Estadistica(idEstadistica, idJugadora, idPartido, ataques, saques, bloqueos, defensas, errores);
    }
}
