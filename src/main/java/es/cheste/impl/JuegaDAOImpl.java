package es.cheste.impl;

import es.cheste.clases.Juega;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.JuegaDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar la participación de las jugadoras en los distintos partidos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link JuegaDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Juega} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Juega
 * @see JuegaDAO
 */
public class JuegaDAOImpl implements JuegaDAO {
    private static final String INSERTAR = "INSERT INTO JUEGA (ID_JUGADORA, ID_PARTIDO, MINUTO_ENTRADA, MINUTO_SALIDA, ESTADO) VALUES (?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM JUEGA WHERE ID_JUGADORA = ? AND ID_PARTIDO = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM JUEGA";
    private static final String ACTUALIZAR = "UPDATE JUEGA SET MINUTO_ENTRADA = ?, MINUTO_SALIDA = ?, ESTADO = ? WHERE ID_JUGADORA = ? AND ID_PARTIDO = ?";
    private static final String ELIMINAR = "DELETE FROM JUEGA WHERE ID_JUGADORA = ? AND ID_PARTIDO = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta una nueva participación de una jugadora en el partido correspondiente en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de la participación a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente a la jugadora y al partido.</p>
     *
     * @param juega es el objeto {@link Juega} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar la participación.
     */
    @Override
    public void insertar(Juega juega) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR)) {

            ps.setInt(1, juega.getIdJugadora());
            ps.setInt(2, juega.getIdPartido());
            ps.setInt(3, juega.getMinutoEntrada());
            ps.setInt(4, juega.getMinutoSalida());
            ps.setString(5, juega.getEstado());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se ha podido insertar la participación de la jugadora en el partido correspondiente, no se afectaron filas.", null);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar los datos de la participación de la jugadora en el partido correspondiente.", e);
        }
    }

    /**
     * Obtiene la participación de la jugadora en el partido correspondiente de la base de datos utilizando los identificadores de ambos atributos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de la participación con los identificadores correspondientes.
     * Si se encuentra el registro, se crea un objeto {@link Juega} con los datos recuperados.</p>
     *
     * @param idJugadora el ID de la jugadora que se desea obtener.
     * @param idPartido  el ID del partido que se desea obtener.
     * @return un objeto {@link Juega} si se encuentra la participación, en caso contrario, devuelve un valor null.
     * @throws DAOException si ocurre un error SQL al recuperar la participación.
     */
    @Override
    public Juega obtenerPorId(int idJugadora, int idPartido) throws DAOException {
        Juega juega = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, idJugadora);
            ps.setInt(2, idPartido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    juega = mapearJuega(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener la participación de la jugadora en el partido correspondiente.", e);
        }
        return juega;
    }

    /**
     * Obtiene todas las participaciones de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla JUEGA.
     * Los registros recuperados se agregan a una lista de objetos {@link Juega}.</p>
     *
     * @return una lista de objetos {@link Juega} que representa todas las participaciones en la base de datos.
     * @throws DAOException si ocurre un error SQL al recuperar las participaciones.
     */
    @Override
    public List<Juega> obtenerTodos() throws DAOException {
        List<Juega> juegaList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Juega juega = mapearJuega(rs);
                juegaList.add(juega);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todas las participaciones de las jugadoras en los partidos.", e);
        }
        return juegaList;
    }

    /**
     * Actualiza la información de la participación de una jugadora en el partido correspondiente en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de una participación a partir de su identificadores correspondientes.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param juega el objeto {@link Juega} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar la participación.
     */
    @Override
    public void actualizar(Juega juega) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, juega.getMinutoEntrada());
            ps.setInt(2, juega.getMinutoSalida());
            ps.setString(3, juega.getEstado());
            ps.setInt(4, juega.getIdJugadora());
            ps.setInt(5, juega.getIdPartido());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de la participación de la jugadora en el partido correspondiente, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla JUEGA correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla JUEGA.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla JUEGA: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar la participación de la jugadora en el partido.", e);
        }
    }

    /**
     * Elimina la información de la participación de una jugadora en el partido correspondiente de la base de datos utilizando los identificadores.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de una participación con los identificadores correspondientes.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param idJugadora el ID de la jugadora que se desea eliminar.
     * @param idPartido  el ID del partido que se desea eliminar.
     * @throws DAOException sí ocurre un error SQL al eliminar la participación.
     */
    @Override
    public void eliminar(int idJugadora, int idPartido) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, idJugadora);
            ps.setInt(2, idPartido);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación de la participación de la jugadora en el partido, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla JUEGA correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla JUEGA.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla JUEGA: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar la participación de la jugadora en el partido.", e);
        }
    }

    /**
     * Crea un objeto {@link Juega} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Juega} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos de la participación de una jugadora con el partido correspondiente.
     * @return un objeto {@link Juega} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Juega mapearJuega(ResultSet rs) throws SQLException {
        int idJugadora = rs.getInt("ID_JUGADORA");
        int idPartido = rs.getInt("ID_PARTIDO");
        int minutoEntrada = rs.getInt("MINUTO_ENTRADA");
        int minutoSalida = rs.getInt("MINUTO_SALIDA");
        String estado = rs.getString("ESTADO");

        return new Juega(idJugadora, idPartido, minutoEntrada, minutoSalida, estado);
    }
}
