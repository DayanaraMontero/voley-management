package es.cheste.impl;

import es.cheste.clases.Jugadora;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.interfaces.JugadoraDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para insertar, actualizar, eliminar y consultar jugadoras en la base de datos. Utiliza
 * la clase {@link ConexionBD} para obtener la conexión y manejar transacciones.
 *
 * <p>Implementa la interfaz {@link JugadoraDAO} para gestionar operaciones de CRUD sobre la entidad
 * {@link Jugadora} en la base de datos.</p>
 *
 * <p>Cada método de esta clase maneja la excepción personalizada {@link DAOException}.</p>
 *
 * @see ConexionBD
 * @see DAOException
 * @see Jugadora
 * @see JugadoraDAO
 */
public class JugadoraDAOImpl implements JugadoraDAO {
    private static final String INSERTAR = "INSERT INTO JUGADORA (NOMBRE, APELLIDOS, POSICION, NACIONALIDAD, EDAD, DORSAL, NOM_EQUIPO, ID_ENTRENADOR) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String OBTENER_POR_ID = "SELECT * FROM JUGADORA WHERE ID_JUGADORA = ?";
    private static final String OBTENER_TODOS = "SELECT * FROM JUGADORA";
    private static final String OBTENER_JUGADORAS_CON_ENTRENADOR = "SELECT J.ID_JUGADORA, J.NOMBRE, E.ID_ENTRENADOR, J.NOM_EQUIPO FROM JUGADORA J, ENTRENADOR E WHERE J.ID_ENTRENADOR = E.ID_ENTRENADOR";
    private static final String ACTUALIZAR = "UPDATE JUGADORA SET NOMBRE = ?, APELLIDOS = ?, POSICION = ?, NACIONALIDAD = ?, EDAD = ?, DORSAL = ?, NOM_EQUIPO = ?, ID_ENTRENADOR = ? WHERE ID_JUGADORA = ?";
    private static final String ELIMINAR = "DELETE FROM JUGADORA WHERE ID_JUGADORA = ?";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    /**
     * Inserta una nueva jugadora en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code INSERTAR} para agregar un registro de una jugadora a la base de datos, si la consulta funciona
     * correctamente, se asigna el ID correspondiente a la jugadora.</p>
     *
     * @param jugadora es el objeto {@link Jugadora} que se desea insertar en la base de datos.
     * @throws DAOException en caso de que ocurra un error SQL al insertar la jugadora.
     */
    @Override
    public void insertar(Jugadora jugadora) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, jugadora.getNombre());
            ps.setString(2, jugadora.getApellidos());
            ps.setString(3, jugadora.getPosicion());
            ps.setString(4, jugadora.getNacionalidad());
            ps.setInt(5, jugadora.getEdad());
            ps.setInt(6, jugadora.getDorsal());
            ps.setString(7, jugadora.getNombreEquipo());
            ps.setInt(8, jugadora.getIdEntrenador());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("No se ha podido insertar la jugadora, no se afectaron filas.", null);
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    jugadora.setIdJugadora(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al insertar los datos de la jugadora.", e);
        }
    }

    /**
     * Obtiene una jugadora de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_POR_ID} para buscar un registro de una jugadora con el ID correspondiente.
     * Si se encuentra el registro, se crea un objeto {@link Jugadora} con los datos recuperados.</p>
     *
     * @param id el ID de la jugadora que se desea obtener.
     * @return un objeto {@link Jugadora} si se encuentra la jugadora, en caso contrario, devuelve un valor null.
     * @throws DAOException si ocurre un error SQL al recuperar la jugadora.
     */
    @Override
    public Jugadora obtenerPorId(int id) throws DAOException {
        Jugadora jugadora = null;
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_POR_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jugadora = mapearJugadora(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener la jugadora por ID.", e);
        }
        return jugadora;
    }

    /**
     * Obtiene todas las jugadoras de la base de datos.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_TODOS} para recuperar todos los registros de la tabla JUGADORA.
     * Los registros recuperados se agregan a una lista de objetos {@link Jugadora}.</p>
     *
     * @return una lista de objetos {@link Jugadora} que representa todas las jugadoras en la base de datos.
     * @throws DAOException sí ocurre un error SQL al recuperar las jugadoras.
     */
    @Override
    public List<Jugadora> obtenerTodos() throws DAOException {
        List<Jugadora> jugadoraList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugadora jugadora = mapearJugadora(rs);
                jugadoraList.add(jugadora);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener todas las jugadoras.", e);
        }
        return jugadoraList;
    }

    /**
     * Obtiene a las jugadoras con su entrenador correspondiente.
     *
     * <p>Se ejecuta la consulta {@code OBTENER_JUGADORAS_CON_ENTRENADOR} para recuperar todos los registros de la tabla JUGADORA
     * donde cada jugadora tenga un entrenador asignado.</p>
     *
     * @return una lista de objetos {@link Jugadora} que representa a las jugadpras con su entrenador específico en la base de datos.
     * @throws DAOException sí ocurre un error SQL al recuperar las jugadoras con su entrenador correspondiente.
     */
    @Override
    public List<Jugadora> obtenerJugadoraConEntrenador() throws DAOException {
        List<Jugadora> jugadoraList = new ArrayList<>();

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(OBTENER_JUGADORAS_CON_ENTRENADOR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugadora jugadora = mapearJugadoraConEntrenador(rs);
                jugadoraList.add(jugadora);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener jugadores con sus correspondientes entrenadores", e);
        }
        return jugadoraList;
    }

    /**
     * Actualiza la información de una jugadora en la base de datos.
     *
     * <p>Se ejecuta la consulta {@code ACTUALIZAR} para modificar los datos de una jugadora a partir de su ID.
     * Si la consulta es exitosa realiza un COMMIT en la transacción, en caso contrario, realiza un ROLLBACK para
     * revertir la transacción.</p>
     *
     * @param jugadora el objeto {@link Jugadora} con los datos actualizados.
     * @throws DAOException sí ocurre un error SQL al actualizar la jugadora.
     */
    @Override
    public void actualizar(Jugadora jugadora) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ACTUALIZAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setString(1, jugadora.getNombre());
            ps.setString(2, jugadora.getApellidos());
            ps.setString(3, jugadora.getPosicion());
            ps.setString(4, jugadora.getNacionalidad());
            ps.setInt(5, jugadora.getEdad());
            ps.setInt(6, jugadora.getDorsal());
            ps.setString(7, jugadora.getNombreEquipo());
            ps.setInt(8, jugadora.getIdEntrenador());
            ps.setInt(9, jugadora.getIdJugadora());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Actualización de jugadora fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para actualizar la fila en la tabla JUGADORA correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para actualizar la fila de la tabla JUGADORA.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para actualizar la fila en la tabla JUGADORA: {}", ex.getMessage());
            }
            throw new DAOException("Error al actualizar la jugadora.", e);
        }
    }

    /**
     * Elimina una jugadora de la base de datos utilizando su ID.
     *
     * <p>Se ejecuta la consulta {@code ELIMINAR} para borrar un registro de una jugadora con el ID correspondiente.
     * Si la consulta es exitosa se realiza un COMMIT en la transacción, en caso contrario, se realiza un ROLLBACK
     * para revertir la transacción.</p>
     *
     * @param id el ID de la jugadora que se desea eliminar.
     * @throws DAOException sí ocurre un error SQL al eliminar la jugadora.
     */
    @Override
    public void eliminar(int id) throws DAOException {
        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(ELIMINAR)) {
            conexionBD.getConnection().setAutoCommit(false);

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DAOException("Eliminación de la jugadora fallida, no se afectaron filas.", null);
            }

            conexionBD.getConnection().commit();
            LOGGER.info("Se ha realizado el COMMIT para eliminar la fila en la tabla JUGADORA correctamente.");

        } catch (SQLException e) {
            try {
                conexionBD.getConnection().rollback();
                LOGGER.info("Se hace ROLLBACK debido a un error para eliminar la fila de la tabla JUGADORA.");
            } catch (SQLException ex) {
                LOGGER.error("Error al intentar hacer ROLLBACK para eliminar la fila en la tabla JUGADORA: {}", ex.getMessage());
            }
            throw new DAOException("Error al eliminar la jugadora.", e);
        }
    }

    /**
     * Crea un objeto {@link Jugadora} a partir de un conjunto de resultados de una consulta SQL.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Jugadora} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos de la jugadora.
     * @return un objeto {@link Jugadora} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Jugadora mapearJugadora(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_JUGADORA");
        String nombre = rs.getString("NOMBRE");
        String apellidos = rs.getString("APELLIDOS");
        String posicion = rs.getString("POSICION");
        String nacionalidad = rs.getString("NACIONALIDAD");
        int edad = rs.getInt("EDAD");
        int dorsal = rs.getInt("DORSAL");
        String nombreEquipo = rs.getString("NOM_EQUIPO");
        int idEntrenador = rs.getInt("ID_ENTRENADOR");

        return new Jugadora(id, nombre, apellidos, posicion, nacionalidad, edad, dorsal, nombreEquipo, idEntrenador);
    }

    /**
     * Crea un objeto {@link Jugadora} a partir de un conjunto de resultados de una consulta SQL, para obtener cada jugadora con su entrenador correspondiente.
     *
     * <p>Se utiliza para mapear los datos obtenidos de una fila en el Resulset a un objeto
     * {@link Jugadora} con los valores correspondientes.</p>
     *
     * @param rs contiene los datos de la jugadora especifícados en la consulta {@code OBTENER_JUGADORAS_CON_ENTRENADOR}.
     * @return un objeto {@link Jugadora} con los datos obtenidos del ResultSet.
     * @throws SQLException sí ocurre un error al leer los datos del ResultSet.
     */
    private Jugadora mapearJugadoraConEntrenador(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_JUGADORA");
        String nombre = rs.getString("NOMBRE");
        int idEntrenador = rs.getInt("ID_ENTRENADOR");
        String nombreEquipo = rs.getString("NOM_EQUIPO");

        return new Jugadora(id, nombre, null, null, null, 0, 0, nombreEquipo, idEntrenador);
    }
}
