package es.cheste.impl;

import es.cheste.clases.Opinion;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.OpinionDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar opiniones en la base de datos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link OpinionDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Opinion} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Opinion
 * @see OpinionDAO
 */
public class OpinionDAOImpl implements OpinionDAO {
    private static final String INSERTAR = "INSERT INTO OPINION (ID_PARTIDO, ID_JUGADORA, ID_USUARIO, PUNTUACION, COMENTARIO) VALUES (?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM OPINION WHERE ID_OPINION = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM OPINION";
    private static final String OBTENER_OPINIONES = "SELECT J.ID_JUGADORA, U.ID_USUARIO, OP.PUNTUACION, OP.COMENTARIO FROM USUARIO U LEFT JOIN OPINION OP ON U.ID_USUARIO = OP.ID_USUARIO LEFT JOIN JUGADORA J ON OP.ID_JUGADORA = J.ID_JUGADORA";
    private static final String ACTUALIZAR = "UPDATE OPINION SET ID_PARTIDO = ?, ID_JUGADORA = ?, ID_USUARIO = ?, PUNTUACION = ?, COMENTARIO = ? WHERE ID_OPINION = ?";
    private static final String ELIMINAR = "DELETE FROM OPINION WHERE ID_OPINION = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta una nueva opinión en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de una opinión a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente a la opinión.</p>
     *
     * @param opinion es el objeto {@link Opinion} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar la opinión.
     */
    @Override
    public void insertar(Opinion opinion) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, opinion.getIdPartido());
            ps.setInt(2, opinion.getIdJugadora());
            ps.setInt(3, opinion.getIdUsuario());
            ps.setInt(4, opinion.getPuntuacion());
            ps.setString(5, opinion.getComentario());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se ha podido insertar la opinión, no se afectaron filas.", null);
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    opinion.setIdOpinion(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar los datos de la opinión.", e);
        }
    }

    /**
     * Obtiene una opinión de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de una opinión con el ID correspondiente.
     * Si se encuentra el registro, se crea un objeto {@link Opinion} con los datos recuperados.</p>
     *
     * @param id el ID de la opinión que se desea obtener.
     * @return un objeto {@link Opinion} si se encuentra la opinión, en caso contrario, devuelve un valor null.
     * @throws DAOException si ocurre un error SQL al recuperar la opinión.
     */
    @Override
    public Opinion obtenerPorId(int id) throws DAOException {
        Opinion opinion = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    opinion = mapearOpinion(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener la opinión por ID.", e);
        }
        return opinion;
    }

    /**
     * Obtiene todas las opiniones de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla OPINION.
     * Los registros recuperados se agregan a una lista de objetos {@link Opinion}.</p>
     *
     * @return una lista de objetos {@link Opinion} que representa todas las opiniones en la base de datos.
     * @throws DAOException si ocurre un error SQL al recuperar las opiniones.
     */
    @Override
    public List<Opinion> obtenerTodos() throws DAOException {
        List<Opinion> opinionList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Opinion opinion = mapearOpinion(rs);
                opinionList.add(opinion);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todas las opiniones.", e);
        }
        return opinionList;
    }

    /**
     * Obtiene todas las opiniones de los usuarios con respecto a su jugadora favorita.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_OPINIONES} para recuperar las opiniones de los usuarios sobre su jugadora favorita de la tabla OPINION.
     * Los registros recuperados se agregan a una lista de objetos {@link Opinion}.</p>
     *
     * @return una lista de objetos {@link Opinion} que representa todas las opiniones de los usuarios sobre su jugadora favorita.
     * @throws DAOException sí ocurre un error SQL al recuperar las opiniones de los usuarios sobre su jugadora favorita.
     */
    @Override
    public List<Opinion> obtenerOpiniones() throws DAOException {
        List<Opinion> opinionList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_OPINIONES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Opinion opinion = mapearOpinionUsuario(rs);
                opinionList.add(opinion);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener las opiniones de los usuarios sobre su jugadora favorita.", e);
        }
        return opinionList;
    }

    /**
     * Actualiza la información de una opinión en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de una opinión a partir de su ID.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param opinion el objeto {@link Opinion} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar la opinión.
     */
    @Override
    public void actualizar(Opinion opinion) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, opinion.getIdPartido());
            ps.setInt(2, opinion.getIdJugadora());
            ps.setInt(3, opinion.getIdUsuario());
            ps.setInt(4, opinion.getPuntuacion());
            ps.setString(5, opinion.getComentario());
            ps.setInt(6, opinion.getIdOpinion());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de la opinión fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla OPINION correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla OPINION.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla OPINION: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar la opinión.", e);
        }
    }

    /**
     * Elimina una opinión de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de una opinión con el ID correspondiente.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param id el ID de la opinión que se desea eliminar.
     * @throws DAOException si ocurre un error SQL al eliminar la opinión.
     */
    @Override
    public void eliminar(int id) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {

            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación de la opinión fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla OPINION correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla OPINION.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla OPINION: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar la opinión.", e);
        }
    }

    /**
     * Crea un objeto {@link Opinion} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Opinion} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos de la opinión.
     * @return un objeto {@link Opinion} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Opinion mapearOpinion(ResultSet rs) throws SQLException {
        int idOpinion = rs.getInt("ID_OPINION");
        int idPartido = rs.getInt("ID_PARTIDO");
        int idJugadora = rs.getInt("ID_JUGADORA");
        int idUsuario = rs.getInt("ID_JUGADORA");
        int puntuacion = rs.getInt("PUNTUACION");
        String comentario = rs.getString("COMENTARIO");

        return new Opinion(idOpinion, idPartido, idJugadora, idUsuario, puntuacion, comentario);
    }

    /**
     * Crea un objeto {@link Opinion} a partir de un conjunto de resultados de una consulta SQL, para obtener las opiniones de los usuarios
     * de su respectiva jugadora favorita.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Opinion} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos de las opiniones de los usuarios sobre su jugadora favorita.
     * @return un objeto {@link Opinion} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Opinion mapearOpinionUsuario(ResultSet rs) throws SQLException {
        int idJugadora = rs.getInt("ID_JUGADORA");
        int idUsuario = rs.getInt("ID_USUARIO");
        int puntuacion = rs.getInt("PUNTUACION");
        String comentario = rs.getString("COMENTARIO");

        return new Opinion(0, idJugadora, idUsuario, puntuacion, comentario);
    }
}
