package es.cheste.impl;

import es.cheste.clases.Sigue;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.SigueDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar a las jugadoras que los usuarios siguen. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link SigueDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link SigueDAO} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Sigue
 * @see SigueDAO
 */
public class SigueDAOImpl implements SigueDAO {
    private static final String INSERTAR = "INSERT INTO SIGUE (ID_USUARIO, ID_JUGADORA, FECHA_SEGUIMIENTO, FRECUENCIA_INTERACCION, OBSERVACIONES) VALUES (?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM SIGUE WHERE ID_USUARIO = ? AND ID_JUGADORA = ? AND FECHA_SEGUIMIENTO = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM SIGUE";
    private static final String ACTUALIZAR = "UPDATE SIGUE SET FRECUENCIA_INTERACCION = ?, OBSERVACIONES = ? WHERE ID_USUARIO = ? AND ID_JUGADORA = ? AND FECHA_SEGUIMIENTO = ?";
    private static final String ELIMINAR = "DELETE FROM SIGUE WHERE ID_USUARIO = ? AND ID_JUGADORA = ? AND FECHA_SEGUIMIENTO = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta un nuevo seguimiento de un usuario sobre su jugadora favorita.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de un seguimiento a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente al usuario y a la jugadora.</p>
     *
     * @param sigue es el objeto {@link Sigue} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar el seguimiento del usuario.
     */
    @Override
    public void insertar(Sigue sigue) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR)) {
            LocalDate fecha = sigue.getFechaSeguimiento();
            Date fechaSql = Date.valueOf(fecha);

            ps.setInt(1, sigue.getIdUsuario());
            ps.setInt(2, sigue.getIdJugadora());
            ps.setDate(3, fechaSql);
            ps.setString(4, sigue.getFrecuenciaInteraccion());
            ps.setString(5, sigue.getObservaciones());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se ha podido insertar el seguimiento del usuario sobre su jugadora favorita, no se afectaron filas.", null);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar el seguimiento del usuario sobre su jugadora favorita.", e);
        }
    }

    /**
     * Obtiene el seguimiento del usuario sobre su jugadora favorita, utilizando la fecha de seguimiento y los identificadores de ambos atributos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro del seguimiento con la fecha de seguimiento y los identificadores correspondientes.
     * Si se encuentra el registro, se crea un objeto {@link Sigue} con los datos recuperados.</p>
     *
     * @param idUsuario        el ID del usuario que se desea obtener.
     * @param idJugadora       el ID de la jugadora que se desea obtener.
     * @param fechaSeguimiento la fecha en la que el usuario empieza a seguir a la jugadora.
     * @return un objeto {@link Sigue} si se encuentra el seguimiento, en caso contrario, devuelve un valor null.
     * @throws DAOException sí ocurre un error SQL al recuperar el seguimiento.
     */
    @Override
    public Sigue obtenerPorId(int idUsuario, int idJugadora, LocalDate fechaSeguimiento) throws DAOException {
        Sigue sigue = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idJugadora);
            ps.setDate(3, Date.valueOf(fechaSeguimiento));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sigue = mapearSigue(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener el seguimiento del usuario sobre su jugadora favorita.", e);
        }
        return sigue;
    }

    /**
     * Obtiene todos los seguimientos de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla SIGUE.
     * Los registros recuperados se agregan a una lista de objetos {@link Sigue}.</p>
     *
     * @return una lista de objetos {@link Sigue} que representa todos los seguimientos en la base de datos.
     * @throws DAOException si ocurre un error SQL al recuperar los seguimientos.
     */
    @Override
    public List<Sigue> obtenerTodos() throws DAOException {
        List<Sigue> sigueList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sigue sigue = mapearSigue(rs);
                sigueList.add(sigue);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los seguimientos de los usuarios sobre sus jugadoras favoritas.", e);
        }
        return sigueList;
    }

    /**
     * Actualiza la información del seguimiento de un usuario sobre su jugadora favorita.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de un seguimiento a partir de la fecha de seguimiento y los identificadores correspondientes.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param sigue el objeto {@link Sigue} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar el seguimiento.
     */
    @Override
    public void actualizar(Sigue sigue) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            LocalDate fecha = sigue.getFechaSeguimiento();
            Date fechaSql = Date.valueOf(fecha);

            ps.setString(1, sigue.getFrecuenciaInteraccion());
            ps.setString(2, sigue.getObservaciones());
            ps.setInt(3, sigue.getIdUsuario());
            ps.setInt(4, sigue.getIdJugadora());
            ps.setDate(5, fechaSql);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización del seguimiento del usuario sobre su jugadora favorita, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla SIGUE correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla SIGUE.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla SIGUE: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar la informacion del usuario respecto a la jugadora.", e);
        }
    }

    /**
     * Elimina la información del seguimiento de un usuario sobre su jugadora favorita utilizando la fecha de seguimiento y los identificadores correspondientes.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de un seguimiento con la fecha de seguimiento y los identificadores correspondientes.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param idUsuario        el ID del usuario que se desea eliminar.
     * @param idJugadora       el ID de la jugadora que se desea eliminar.
     * @param fechaSeguimiento la fecha en la que el usuario empieza a seguir a la jugadora.
     * @throws DAOException sí ocurre un error SQL al eliminar el seguimiento.
     */
    @Override
    public void eliminar(int idUsuario, int idJugadora, LocalDate fechaSeguimiento) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {

            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, idUsuario);
            ps.setInt(2, idJugadora);
            ps.setDate(3, Date.valueOf(fechaSeguimiento));

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación del seguimiento del usuario sobre su jugadora favorita, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla SIGUE correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla SIGUE.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla SIGUE: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar la informacion del usuario respecto a la jugadora.", e);
        }
    }

    /**
     * Crea un objeto {@link Sigue} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Sigue} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos del seguimiento del usuario sobre su jugadora favorita.
     * @return un objeto {@link Sigue} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Sigue mapearSigue(ResultSet rs) throws SQLException {
        int idUsuario = rs.getInt("ID_USUARIO");
        int idJugadora = rs.getInt("ID_JUGADORA");
        Date fechaSql = rs.getDate("FECHA_SEGUIMIENTO");
        LocalDate fecha = (fechaSql != null) ? fechaSql.toLocalDate() : null;
        String frecuenciaInteraccion = rs.getString("FRECUENCIA_INTERACCION");
        String observaciones = rs.getString("OBSERVACIONES");

        return new Sigue(idUsuario, idJugadora, fecha, frecuenciaInteraccion, observaciones);
    }
}
