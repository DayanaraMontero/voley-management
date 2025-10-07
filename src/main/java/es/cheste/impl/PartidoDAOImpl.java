package es.cheste.impl;

import es.cheste.clases.Partido;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.PartidoDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar partidos en la base de datos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link PartidoDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Partido} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Partido
 * @see PartidoDAO
 */
public class PartidoDAOImpl implements PartidoDAO {
    private static final String INSERTAR = "INSERT INTO PARTIDO (FECHA, EQUIPO_COMPETIDOR1, ENTRENADOR_COMPETIDOR1, EQUIPO_COMPETIDOR2, ENTRENADOR_COMPETIDOR2, RESULTADO, DURACION) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM PARTIDO WHERE ID_PARTIDO = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM PARTIDO";
    private static final String ACTUALIZAR = "UPDATE PARTIDO SET FECHA = ?, EQUIPO_COMPETIDOR1 = ?, ENTRENADOR_COMPETIDOR1 = ?, EQUIPO_COMPETIDOR2 = ?, ENTRENADOR_COMPETIDOR2 = ?, RESULTADO = ?, DURACION = ? WHERE ID_PARTIDO = ?";
    private static final String ELIMINAR = "DELETE FROM PARTIDO WHERE ID_PARTIDO = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta un nuevo partido en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de un partido a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente al partido.</p>
     *
     * @param partido es el objeto {@link Partido} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar el partido.
     */
    @Override
    public void insertar(Partido partido) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate fecha = partido.getFecha();
            Date fechaSql = Date.valueOf(fecha);

            ps.setDate(1, fechaSql);
            ps.setString(2, partido.getEquipoCompetidor1());
            ps.setInt(3, partido.getEntrenadorCompetidor1());
            ps.setString(4, partido.getEquipoCompetidor2());
            ps.setInt(5, partido.getEntrenadorCompetidor2());
            ps.setString(6, partido.getResultado());
            ps.setInt(7, partido.getDuracion());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se han podido insertar datos del partido, no se afectaron filas.", null);
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    partido.setIdPartido(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar los datos del partido.", e);
        }
    }

    /**
     * Obtiene un partido de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de un partido con el ID correspondiente.
     * Si se encuentra el registro, se crea un objeto {@link Partido} con los datos recuperados.</p>
     *
     * @param id el ID del partido que se desea obtener.
     * @return un objeto {@link Partido} si se encuentra el partido, en caso contrario, devuelve un valor null.
     * @throws DAOException sí ocurre un error SQL al recuperar el partido.
     */
    @Override
    public Partido obtenerPorId(int id) throws DAOException {
        Partido partido = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    partido = mapearPartido(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener el partido por ID.", e);
        }
        return partido;
    }

    /**
     * Obtiene todos los partidos de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla PARTIDO.
     * Los registros recuperados se agregan a una lista de objetos {@link Partido}.</p>
     *
     * @return una lista de objetos {@link Partido} que representa todos los partidos en la base de datos.
     * @throws DAOException si ocurre un error SQL al recuperar los partidos.
     */
    @Override
    public List<Partido> obtenerTodos() throws DAOException {
        List<Partido> partidoList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Partido partido = mapearPartido(rs);
                partidoList.add(partido);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los partidos.", e);
        }
        return partidoList;
    }

    /**
     * Actualiza la información de un partido en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de un partido a partir de su ID.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param partido el objeto {@link Partido} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar el partido.
     */
    @Override
    public void actualizar(Partido partido) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            LocalDate fecha = partido.getFecha();
            Date fechaSql = Date.valueOf(fecha);

            ps.setDate(1, fechaSql);
            ps.setString(2, partido.getEquipoCompetidor1());
            ps.setString(3, partido.getEquipoCompetidor1());
            ps.setString(4, partido.getEquipoCompetidor2());
            ps.setInt(5, partido.getEntrenadorCompetidor2());
            ps.setString(6, partido.getResultado());
            ps.setInt(7, partido.getDuracion());
            ps.setInt(8, partido.getIdPartido());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de partido fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla PARTIDO correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla PARTIDO.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla PARTIDO: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar el partido.", e);
        }
    }

    /**
     * Elimina un partido de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de un partido con el ID correspondiente.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param id el ID del partido que se desea eliminar.
     * @throws DAOException sí ocurre un error SQL al eliminar el partido.
     */
    @Override
    public void eliminar(int id) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación del partido fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla PARTIDO correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla PARTIDO.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla PARTIDO: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar el partido.", e);
        }
    }

    /**
     * Crea un objeto {@link Partido} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Partido} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos del partido.
     * @return un objeto {@link Partido} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Partido mapearPartido(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_PARTIDO");
        Date fechaSql = rs.getDate("FECHA");
        LocalDate fecha = (fechaSql != null) ? fechaSql.toLocalDate() : null;
        String equipoCompetidor1 = rs.getString("EQUIPO_COMPETIDOR1");
        int entrenadorCompetidor1 = rs.getInt("ENTRENADOR_COMPETIDOR1");
        String equipoCompetidor2 = rs.getString("EQUIPO_COMPETIDOR2");
        int entrenadorCompetidor2 = rs.getInt("ENTRENADOR_COMPETIDOR2");
        String resultado = rs.getString("RESULTADO");
        int duracion = rs.getInt("DURACION");

        return new Partido(id, fecha, equipoCompetidor1, entrenadorCompetidor1, equipoCompetidor2, entrenadorCompetidor2, resultado, duracion);
    }
}
